package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.*
import com.natpryce.krouton.simple.by
import com.natpryce.krouton.simple.otherwise
import com.natpryce.krouton.simple.routeOn
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_BAD_METHOD
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


fun counterIn(counters: ConcurrentHashMap<String, AtomicInteger>) = object : Projection1<String,AtomicInteger> {
    override fun fromParts(t: String) = counters.getOrPut(t) { AtomicInteger(0) }
    override fun toParts(u: AtomicInteger) : Nothing = throw UnsupportedOperationException("reverse routing not used")
}

fun counterServer(port: Int = 0): HttpServer {
    fun HttpExchange.sendInt(n: Int) = sendString(n.toString())

    val counters = ConcurrentHashMap<String, AtomicInteger>()

    val namedCounter = "counter" / string asA counterIn(counters)
    val namedCounterValue = namedCounter / int

    return HttpServer(port) { exchange ->
        routeOn(exchange.requestURI.path,
                namedCounter by { counter ->
                    when (exchange.requestMethod) {
                        "GET" -> {
                            exchange.sendInt(counter.get())
                        }
                        else -> {
                            exchange.sendError(HTTP_BAD_METHOD)
                        }
                    }
                },
                namedCounterValue by { val (counter, value) = it
                    when (exchange.requestMethod) {
                        "POST" -> {
                            exchange.sendInt(counter.addAndGet(value))
                        }
                        "PUT" -> {
                            counter.set(value)
                            exchange.sendInt(value)
                        }
                        else -> {
                            exchange.sendError(HTTP_BAD_METHOD)
                        }
                    }
                }
        ) otherwise {
            exchange.sendError(HTTP_NOT_FOUND)
        }
    }
}

class CountersTest {
    val server = counterServer()

    @Before
    fun startServer() {
        server.start()
    }

    @After
    fun stopServer() {
        server.stop(0)
    }

    @Test
    fun counting() {
        assertThat(http("PUT", "/counter/a/0"), equalTo(0))
        assertThat(http("GET", "/counter/a"), equalTo(0))
        assertThat(http("POST", "/counter/a/1"), equalTo(1))
        assertThat(http("GET", "/counter/a"), equalTo(1))
        assertThat(http("POST", "/counter/a/2"), equalTo(3))
        assertThat(http("GET", "/counter/a"), equalTo(3))
        assertThat(http("POST", "/counter/a/-3"), equalTo(0))
        assertThat(http("GET", "/counter/a"), equalTo(0))
    }

    @Test
    fun can_set_count() {
        http("POST", "/counter/b/10")
        http("PUT", "/counter/b/2")
        assertThat(http("GET", "/counter/b"), equalTo(2))
        assertThat(http("POST", "/counter/b/5"), equalTo(7))
    }

    @Test
    fun defaults_to_zero_when_counting() {
        assertThat(http("POST", "/counter/c/1"), equalTo(1))
    }

    private fun http(method: String, path: String) = (server.uri.resolve(path).toURL().openConnection() as HttpURLConnection)
            .run {
                requestMethod = method
                inputStream.reader().readText().trim().toInt()
            }
}

