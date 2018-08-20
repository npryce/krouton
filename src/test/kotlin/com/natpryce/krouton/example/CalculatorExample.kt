package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.PathTemplate2
import com.natpryce.krouton.double
import com.natpryce.krouton.http4k.resources
import com.natpryce.krouton.path
import com.natpryce.krouton.plus
import com.natpryce.krouton.unaryPlus
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.MOVED_PERMANENTLY
import org.http4k.core.Status.Companion.OK
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URI


val operands: PathTemplate2<Double, Double> = double + double

val add: PathTemplate2<Double, Double> = +"add" + operands
val sub: PathTemplate2<Double, Double> = +"sub" + operands
val mul: PathTemplate2<Double, Double> = +"mul" + operands
val div: PathTemplate2<Double, Double> = +"div" + operands
val max: PathTemplate2<Double, Double> = +"max" + operands
val min: PathTemplate2<Double, Double> = +"min" + operands
val avg: PathTemplate2<Double, Double> = +"avg" + operands

fun calculatorServer(): HttpHandler {
    return resources {
        add methods {
            GET { _, (x, y) -> ok(x + y) }
        }
        sub methods {
            GET { _, (x, y) -> ok(x - y) }
        }
        mul methods {
            GET { _, (x, y) -> ok(x * y) }
        }
        div methods {
            GET { _, (x, y) -> ok(x / y) }
        }
        max methods {
            GET { _, (x, y) -> ok(maxOf(x, y)) }
        }
        min methods {
            GET { _, (x, y) -> ok(minOf(x, y)) }
        }
        avg methods {
            GET { _, (x, y) -> ok((x + y) / 2.0) }
        }
        // reverse routing: turn a path template into a link
        +"average" + operands methods {
            GET { _, (x,y) -> Response(MOVED_PERMANENTLY).header("Location", avg.path(x, y)) }
        }
    }
}

private fun ok(value: Double) = Response(OK).body(value.toString())

class CountersTest {
    @Test
    fun counting() {
        assertThat(http(GET, "/add/1/2"), equalTo("3.0"))
        assertThat(http(GET, "/sub/2.25/0.5"), equalTo("1.75"))
    }
    
    private fun http(method: Method, path: String) =
        (serverUri.resolve(path).toURL().openConnection() as HttpURLConnection)
            .run {
                requestMethod = method.name
                inputStream.reader().readText().trim()
            }
    
    companion object {
        private val port = 8954
        private val server = calculatorServer().asServer(SunHttp(port))
        val serverUri = URI("http://127.0.0.1:$port/")
        
        @BeforeClass
        @JvmStatic
        fun startServer() {
            server.start()
        }
        
        @AfterClass
        @JvmStatic
        fun stopServer() {
            server.stop()
        }
    }
    
}

