//package com.natpryce.krouton.example
//
//import com.natpryce.hamkrest.assertion.assertThat
//import com.natpryce.hamkrest.containsSubstring
//import com.natpryce.hamkrest.equalTo
//import com.natpryce.hamkrest.has
//import com.natpryce.hamkrest.present
//import com.natpryce.hamkrest.throws
//import com.natpryce.krouton.Projection
//import com.natpryce.krouton.asA
//import com.natpryce.krouton.component1
//import com.natpryce.krouton.component2
//import com.natpryce.krouton.component3
//import com.natpryce.krouton.int
//import com.natpryce.krouton.plus
//import com.natpryce.krouton.root
//import com.natpryce.krouton.simple.by
//import com.natpryce.krouton.simple.otherwise
//import com.natpryce.krouton.simple.routeOn
//import com.natpryce.krouton.string
//import com.sun.net.httpserver.HttpExchange
//import com.sun.net.httpserver.HttpServer
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import java.io.IOException
//import java.net.HttpURLConnection
//import java.net.HttpURLConnection.HTTP_BAD_METHOD
//import java.net.HttpURLConnection.HTTP_CONFLICT
//import java.net.HttpURLConnection.HTTP_NOT_FOUND
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.atomic.AtomicInteger
//
//
//fun counterIn(counters: ConcurrentHashMap<String, AtomicInteger>) = object : Projection<String,AtomicInteger> {
//    override fun fromParts(parts: String) = counters.getOrPut(parts) { AtomicInteger(0) }
//    override fun toParts(mapped: AtomicInteger) : Nothing = throw UnsupportedOperationException("reverse routing not used")
//}
//
//fun counterServer(port: Int = 0): HttpServer {
//    fun HttpExchange.sendInt(n: Int) = sendString(n.toString())
//
//    val counters = ConcurrentHashMap<String, AtomicInteger>()
//
//    val namedCounter = root + "counter" + string asA counterIn(counters)
//    val namedCounterValue = namedCounter + int
//    val namedCounterCompareAndSet = namedCounter + "from" + int + "to" + int
//
//    return HttpServer(port) { exchange ->
//        routeOn(exchange.requestURI.path,
//                namedCounter by { counter: AtomicInteger ->
//                    when (exchange.requestMethod) {
//                        "GET" -> {
//                            exchange.sendInt(counter.get())
//                        }
//                        else -> {
//                            exchange.sendError(HTTP_BAD_METHOD)
//                        }
//                    }
//                },
//                namedCounterValue by { (counter: AtomicInteger, value: Int) ->
//                    when (exchange.requestMethod) {
//                        "POST" -> {
//                            exchange.sendInt(counter.addAndGet(value))
//                        }
//                        "PUT" -> {
//                            counter.set(value)
//                            exchange.sendInt(value)
//                        }
//                        else -> {
//                            exchange.sendError(HTTP_BAD_METHOD)
//                        }
//                    }
//                },
//                namedCounterCompareAndSet by { (counter: AtomicInteger, fromValue: Int, toValue: Int) ->
//                    when (exchange.requestMethod) {
//                        "POST", "PUT" -> {
//                            if (counter.compareAndSet(fromValue, toValue)) {
//                                exchange.sendOk()
//                            }
//                            else {
//                                exchange.sendError(HTTP_CONFLICT)
//                            }
//                        }
//                    }
//                }
//        ) otherwise {
//            exchange.sendError(HTTP_NOT_FOUND)
//        }
//    }
//}
//
//class CountersTest {
//    val server = counterServer()
//
//    @Before
//    fun startServer() {
//        server.start()
//    }
//
//    @After
//    fun stopServer() {
//        server.stop(0)
//    }
//
//    @Test
//    fun counting() {
//        assertThat(http("PUT", "/counter/a/0"), equalTo("0"))
//        assertThat(http("GET", "/counter/a"), equalTo("0"))
//        assertThat(http("POST", "/counter/a/1"), equalTo("1"))
//        assertThat(http("GET", "/counter/a"), equalTo("1"))
//        assertThat(http("POST", "/counter/a/2"), equalTo("3"))
//        assertThat(http("GET", "/counter/a"), equalTo("3"))
//        assertThat(http("POST", "/counter/a/-3"), equalTo("0"))
//        assertThat(http("GET", "/counter/a"), equalTo("0"))
//    }
//
//    @Test
//    fun can_set_count() {
//        http("POST", "/counter/b/10")
//        http("PUT", "/counter/b/2")
//        assertThat(http("GET", "/counter/b"), equalTo("2"))
//        assertThat(http("POST", "/counter/b/5"), equalTo("7"))
//    }
//
//    @Test
//    fun defaults_to_zero_when_counting() {
//        assertThat(http("POST", "/counter/c/1"), equalTo("1"))
//    }
//
//    @Test
//    fun compare_and_set() {
//        http("PUT", "/counter/d/10")
//
//        http("POST", "/counter/d/from/10/to/20")
//        assertThat(http("GET", "/counter/d"), equalTo("20"))
//
//        assertThat({http("POST", "/counter/d/from/10/to/30")}, throws<IOException>(
//                has(Throwable::message, present(containsSubstring("response code: 409")))))
//        assertThat(http("GET", "/counter/d"), equalTo("20"))
//    }
//
//    private fun http(method: String, path: String) = (server.uri.resolve(path).toURL().openConnection() as HttpURLConnection)
//            .run {
//                requestMethod = method
//                inputStream.reader().readText().trim()
//            }
//}
//
