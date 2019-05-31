package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.double
import com.natpryce.krouton.int
import com.natpryce.krouton.path
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import com.natpryce.krouton.string
import com.natpryce.krouton.unaryPlus
import dev.minutest.rootContext
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.METHOD_NOT_ALLOWED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.platform.commons.annotation.Testable


private val incrementInt = +"inc" + int.named("i")
private val incrementDouble = +"inc" + double.named("d")
private val a = +"a"
private val b = +"b"

@Testable
fun `routing HTTP4K requests via Krouton routes`() = rootContext {
    test("routes by path`") {
        val router = resources {
            incrementInt { _, n ->
                Response(OK).body((n + 1).toString())
            }
        }
        
        val request = Request(GET, "/inc/10")
        val response = router(request)
        
        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("11"))
    }
    
    test("overlapping routes`") {
        val router = resources {
            incrementInt { _, n ->
                Response(OK).body((n + 1).toString())
            }
            incrementDouble { _, d ->
                Response(OK).body((d + 1.0).toString())
            }
        }
        
        assertThat(router(Request(GET, "/inc/10.0")).bodyString(), equalTo("11.0"))
    }
    
    test("route with no parsed path elements`") {
        val router = resources {
            a { Response(OK).body("a") }
            b methods {
                GET { Response(OK).body("b") }
            }
        }
        
        
        assertThat(router(Request(GET, "/a")).bodyString(), equalTo("a"))
        assertThat(router(Request(GET, "/b")).bodyString(), equalTo("b"))
    }
    
    
    test("returns 404 Not Found for unrecognised URI`") {
        val router = resources {
            incrementInt { _, n ->
                Response(OK).body((n + 1).toString())
            }
        }
        
        assertThat(router(Request(GET, "/blah-blah-blah")).status, equalTo(NOT_FOUND))
        assertThat(router(Request(GET, "/inc/not-a-number")).status, equalTo(NOT_FOUND))
        assertThat(router(Request(GET, "/inc/10.0")).status, equalTo(NOT_FOUND))
    }
    
    test("routing by method`") {
        val tester = root + "test" + string.named("s")
        
        val router = resources {
            tester methods {
                GET { _, s -> Response(OK).body("got $s") }
                POST { _, s -> Response(OK).body("posted $s") }
            }
        }
        
        assertThat(router(Request(GET, tester.path("x"))), equalTo(Response(OK).body("got x")))
        assertThat(router(Request(POST, tester.path("y"))), equalTo(Response(OK).body("posted y")))
        assertThat(router(Request(PUT, tester.path("x"))).status, equalTo(METHOD_NOT_ALLOWED))
    }
    
    test("reports routes as url templates`") {
        val router = resources {
            incrementInt { _, _ -> Response(OK) }
            incrementDouble { _, _ -> Response(OK) }
            (+"another") { _, _ -> Response(OK) }
        }
        
        assertThat(router.urlTemplates(), equalTo(listOf(
            "/inc/{i}",
            "/inc/{d}",
            "/another"
        )))
    }
}
