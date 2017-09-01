package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.int
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.Test


class PathRouting {
    val routeInc = int
    
    @Test
    fun `routes by path`() {
        val router = pathRouter(
            listOf(
                routeInc by { n ->
                    Response(OK).body((n+1).toString())
                }
            ))
    
        val request: Request = Request(GET, "/10")
        val response = router(request)
        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("11"))
    }
}
