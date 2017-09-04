package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.HStack2
import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.int
import com.natpryce.krouton.monitored
import com.natpryce.krouton.named
import com.natpryce.krouton.path
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import com.natpryce.krouton.string
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.Test

class MonitoredRoutingTests {
    var lastEvent: Triple<Request, Response, String>? = null
    
    val example: UrlScheme<HStack2<Int, String>> = root + "example" + string.named("name").monitored() + int.named("x")
    
    val monitor : RequestMonitor = { request, response, pathTemplate ->
        lastEvent = Triple(request, response, pathTemplate)
    }
    
    val app = resources(monitor = monitor) {
        example { Response(Status.OK) }
    }
    
    @Test
    fun `reports path template to monitor`() {
        val request = Request(GET, example.path("bob", 10))
        val response = app(request)
        
        assertThat(lastEvent, equalTo(Triple(request, response, "/example/bob/{x}")))
    }
}