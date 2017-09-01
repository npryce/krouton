package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.double
import com.natpryce.krouton.int
import com.natpryce.krouton.path
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import com.natpryce.krouton.string
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.METHOD_NOT_ALLOWED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.Test


class ResourceRoutingTests {
    val incrementInt = root + "inc" + int
    val incrementDouble = root + "inc" + double
    
    @Test
    fun `routes by path`() {
        val router = resources {
            incrementInt { n ->
                Response(OK).body((n + 1).toString())
            }
        }
        
        val request = Request(GET, "/inc/10")
        val response = router(request)
        
        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("11"))
    }
    
    @Test
    fun `overlapping routes`() {
        val router = resources {
            incrementInt { n ->
                Response(OK).body((n + 1).toString())
            }
            incrementDouble { d ->
                Response(OK).body((d + 1.0).toString())
            }
        }
    
        val response = router(Request(GET, "/inc/10.0"))
        assertThat(response.bodyString(), equalTo("11.0"))
    }
    
    @Test
    fun `returns 404 Not Found for unrecognised URI`() {
        val router = resources {
            incrementInt { n ->
                Response(OK).body((n + 1).toString())
            }
        }
    
        assertThat(router(Request(GET, "/blah-blah-blah")).status, equalTo(NOT_FOUND))
        assertThat(router(Request(GET, "/inc/not-a-number")).status, equalTo(NOT_FOUND))
        assertThat(router(Request(GET, "/inc/10.0")).status, equalTo(NOT_FOUND))
    }
    
    @Test
    fun `routing by method`() {
        val tester = root + "test" + string
        
        val router = resources {
            tester by {
                GET { Response(OK).body("got $it") }
                POST { Response(OK).body("posted $it") }
            }
        }
    
        assertThat(router(Request(GET, tester.path("x"))), equalTo(Response(OK).body("got x")))
        assertThat(router(Request(POST, tester.path("y"))), equalTo(Response(OK).body("posted y")))
        assertThat(router(Request(PUT, tester.path("x"))).status, equalTo(METHOD_NOT_ALLOWED))
    }
}
