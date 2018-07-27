package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.cast
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.krouton.PathTemplate2
import com.natpryce.krouton.int
import com.natpryce.krouton.monitoredPath
import com.natpryce.krouton.path
import com.natpryce.krouton.plus
import com.natpryce.krouton.root
import com.natpryce.krouton.string
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.UriTemplate
import org.http4k.routing.RoutedRequest
import org.http4k.routing.RoutedResponse
import org.junit.Test

class MonitoredRoutingTests {
    private val examplePath: PathTemplate2<String, Int> =
        root + "example" + string.named("name").monitored() + int.named("x")
    
    
    @Test
    fun `reports matched path template to app and caller`() {
        val app: ResourceRouter = resources {
            examplePath { rq, parsedElements ->
                val expectedTemplate = UriTemplate.from(examplePath.monitoredPath(parsedElements))
                
                assertThat(rq, cast(has(RoutedRequest::xUriTemplate, equalTo(expectedTemplate))))
                
                Response(Status.OK)
            }
        }
    
        val response = app(Request(GET, examplePath.path("alice", 10)))
        
        assertThat(response, cast(has(RoutedResponse::xUriTemplate, equalTo(UriTemplate.from("/example/alice/{x}")))))
    }
}