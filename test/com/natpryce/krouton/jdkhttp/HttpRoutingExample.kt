package com.natpryce.krouton.jdkhttp

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.*
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.junit.After
import org.junit.Test
import java.io.FileNotFoundException
import java.net.HttpURLConnection.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI


val HttpServer.uri: URI get() = URI("http", null, address.address.hostAddress, address.port, null, null, null)

fun HttpExchange.sendString(s: String) {
    sendResponseHeaders(200, 0)
    responseBody.bufferedWriter().use { w ->
        w.write(s)
    }
}

fun HttpExchange.sendError(statusCode: Int) {
    sendResponseHeaders(statusCode, 0)
    close()
}

fun <T> HttpExchange.sendRedirect(scheme: UrlScheme<T>, value: T) {
    responseHeaders.add("Location", scheme.path(value))
    sendResponseHeaders(HTTP_MOVED_TEMP, 0)
    close()
}

fun willSendError(statusCode: Int) = fun(exchange: HttpExchange) {
    exchange.sendError(statusCode)
}

fun path(httpExchange: HttpExchange) = httpExchange.requestURI.path



class HttpRoutingExample {
    private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0).apply {
        val uppercase = "uppercase"/string where { s -> !s.all(Char::isUpperCase) }
        val reverse = "reverse"/string
        val negate = "negate"/int
        val negative = "negative"/int

        createContext("/",
                routerBy(::path,
                        negate by { exchange, i -> exchange.sendString((-i).toString()) },
                        negative by { exchange, i -> exchange.sendRedirect(negate, i) },

                        uppercase by { exchange, s -> exchange.sendString(s.toUpperCase()) },

                        reverse by { exchange, s -> exchange.sendString(s.reversed()) }
                ) otherwise willSendError(HTTP_NOT_FOUND))
        start()
    }

    @Test
    fun negate() {
        assertThat(getText("/negate/100"), equalTo("-100"))
    }

    @Test
    fun negative_redirects_to_negate() {
        assertThat(getText("/negative/20"), equalTo("-20"))
    }

    @Test
    fun uppercase() {
        assertThat(getText("/uppercase/hello"), equalTo("HELLO"))
    }

    @Test(expected = FileNotFoundException::class)
    fun cannot_uppercase_something_uppercase() {
        getText("/uppercase/HELLO")
    }

    @Test
    fun reverse() {
        assertThat(getText("/reverse/world"), equalTo("dlrow"))
    }

    private fun getText(path: String) = server.uri.resolve(path).toURL()
            .openStream().reader().use { it.readText().trim() }

    @After
    fun stopServer() {
        server.stop(0)
    }
}