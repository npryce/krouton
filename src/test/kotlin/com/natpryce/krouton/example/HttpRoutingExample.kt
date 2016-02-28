package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.*
import com.natpryce.krouton.simple.by
import com.natpryce.krouton.simple.otherwise
import com.natpryce.krouton.simple.routeOn
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.FileNotFoundException
import java.net.HttpURLConnection.HTTP_NOT_FOUND


// The routes
val uppercase = "uppercase" / string where { s -> s.any(Char::isLowerCase) }
val reverse = "reverse" / string
val negate = "negate" / int
val negative = "negative" / int


// The server that uses the routes
val server = HttpServer(0) { exchange ->
    routeOn(exchange.requestURI.rawPath,
            negate by { i ->
                exchange.sendString((-i).toString())
            },

            negative by { i ->
                // Note - reverse routing from integer to URL path
                exchange.sendRedirect(negate.path(i))
            },

            uppercase by { s ->
                exchange.sendString(s.toUpperCase())
            },

            reverse by { s ->
                exchange.sendString(s.reversed())
            },

            root by {
                exchange.sendString("Hello, World.")
            }

    ) otherwise {
        exchange.sendError(HTTP_NOT_FOUND)
    }
}


fun main(args: Array<String>) {
    server.start()
}

class HttpRoutingExample {
    companion object {
        @BeforeClass @JvmStatic
        fun startServer() {
            server.start()
        }

        @AfterClass @JvmStatic
        fun stopServer() {
            server.stop(0)
        }
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
        assertThat(getText("/reverse/hello%20world"), equalTo("dlrow olleh"))
    }

    @Test
    fun root() {
        assertThat(getText("/"), equalTo("Hello, World."))
    }

    private fun getText(path: String) = server.uri.resolve(path).toURL()
            .openStream().reader().use { it.readText().trim() }

}