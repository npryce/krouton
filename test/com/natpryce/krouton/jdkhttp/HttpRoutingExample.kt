package com.natpryce.krouton.jdkhttp

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.*
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.junit.After
import org.junit.Test
import java.io.FileNotFoundException
import java.net.HttpURLConnection.HTTP_NOT_FOUND
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

fun sendError(statusCode: Int) = fun(exchange: HttpExchange) {
    exchange.sendResponseHeaders(statusCode, 0)
    exchange.close()
}

fun path(httpExchange: HttpExchange) = httpExchange.requestURI.path


val uppercase = "uppercase"/string where { s -> !s.all(Char::isUpperCase) }
val reverse = "reverse"/string
val negate = "negate"/int


class HttpRoutingExample {
    private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0).apply {
        createContext("/",
                routeBy(::path,
                        negate by { exchange, i -> exchange.sendString((-i).toString()) },
                        uppercase by { exchange, s -> exchange.sendString(s.toUpperCase()) },
                        reverse by { exchange, s -> exchange.sendString(s.reversed()) }
                ) otherwise sendError(HTTP_NOT_FOUND))
        start()
    }

    @Test
    fun negate() {
        assertThat(getText("/negate/100"), equalTo("-100"))
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