package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.present
import com.natpryce.hamkrest.throws
import com.natpryce.krouton.HStack2
import com.natpryce.krouton.HStack3
import com.natpryce.krouton.Projection
import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.UrlScheme2
import com.natpryce.krouton.UrlScheme3
import com.natpryce.krouton.asA
import com.natpryce.krouton.component1
import com.natpryce.krouton.component2
import com.natpryce.krouton.component3
import com.natpryce.krouton.http4k.resources
import com.natpryce.krouton.int
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import com.natpryce.krouton.string
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


fun counterIn(counters: ConcurrentHashMap<String, AtomicInteger>) = object : Projection<String, AtomicInteger> {
    override fun fromParts(parts: String) = counters.getOrPut(parts) { AtomicInteger(0) }
    override fun toParts(mapped: AtomicInteger): Nothing = throw UnsupportedOperationException("reverse routing not used")
}


fun counterServer(): HttpHandler {
    val counters = ConcurrentHashMap<String, AtomicInteger>()
    
    val namedCounter: UrlScheme<AtomicInteger> = root + "counter" + string.named("counterId").monitored() asA counterIn(counters)
    val namedCounterValue: UrlScheme2<Int, AtomicInteger> = namedCounter + int
    val namedCounterCompareAndSet: UrlScheme3<Int, Int, AtomicInteger> = namedCounter + "from" + int.named("from") + "to" + int.named("to")
    
    return resources {
        namedCounter methods {
            GET { counter ->
                ok(counter.get())
            }
        }
        
        namedCounterValue methods {
            POST { (counter: AtomicInteger, value: Int) ->
                ok(counter.addAndGet(value))
            }
            
            PUT { (counter: AtomicInteger, value: Int) ->
                counter.set(value)
                ok(value)
            }
        }
        
        namedCounterCompareAndSet methods {
            POST { (counter: AtomicInteger, fromValue: Int, toValue: Int) ->
                if (counter.compareAndSet(fromValue, toValue)) {
                    ok(toValue)
                }
                else {
                    Response(Status.CONFLICT)
                }
            }
        }
    }
}

private fun ok(value: Int) = Response(Status.OK).body(value.toString())

class CountersTest {
    @Test
    fun counting() {
        assertThat(http("PUT", "/counter/a/0"), equalTo("0"))
        assertThat(http("GET", "/counter/a"), equalTo("0"))
        assertThat(http("POST", "/counter/a/1"), equalTo("1"))
        assertThat(http("GET", "/counter/a"), equalTo("1"))
        assertThat(http("POST", "/counter/a/2"), equalTo("3"))
        assertThat(http("GET", "/counter/a"), equalTo("3"))
        assertThat(http("POST", "/counter/a/-3"), equalTo("0"))
        assertThat(http("GET", "/counter/a"), equalTo("0"))
    }
    
    @Test
    fun can_set_count() {
        http("POST", "/counter/b/10")
        http("PUT", "/counter/b/2")
        assertThat(http("GET", "/counter/b"), equalTo("2"))
        assertThat(http("POST", "/counter/b/5"), equalTo("7"))
    }
    
    @Test
    fun defaults_to_zero_when_counting() {
        assertThat(http("POST", "/counter/c/1"), equalTo("1"))
    }
    
    @Test
    fun compare_and_set() {
        http("PUT", "/counter/d/10")
        
        http("POST", "/counter/d/from/10/to/20")
        assertThat(http("GET", "/counter/d"), equalTo("20"))
        
        assertThat({ http("POST", "/counter/d/from/10/to/30") }, throws<IOException>(
            has(Throwable::message, present(containsSubstring("response code: 409")))))
        assertThat(http("GET", "/counter/d"), equalTo("20"))
    }
    
    private fun http(method: String, path: String) =
        (serverUri.resolve(path).toURL().openConnection() as HttpURLConnection)
            .run {
                requestMethod = method
                inputStream.reader().readText().trim()
            }
    
    companion object {
        val port = 8954
        val server = counterServer().asServer(SunHttp(port))
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

