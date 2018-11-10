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
import com.oneeyedmen.minutest.junit.JupiterTests
import com.oneeyedmen.minutest.junit.context
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.UriTemplate
import org.http4k.routing.RoutedRequest
import org.http4k.routing.RoutedResponse
import org.junit.jupiter.api.Assertions.assertTrue

class MonitoredRoutingTests : JupiterTests {
    private val examplePath: PathTemplate2<String, Int> =
        root + "example" + string.named("name").monitored() + int.named("x")
    
    
    val app = resources {
        examplePath { rq, parsedElements ->
            val expectedTemplate = UriTemplate.from(examplePath.monitoredPath(parsedElements))
            
            assertThat(rq, cast(has(RoutedRequest::xUriTemplate, equalTo(expectedTemplate))))
            
            Response(Status.OK)
        }
    }
    
    override val tests = context<Unit> {
        test("reports matched path template to app and caller`") {
            val response = app(Request(GET, examplePath.path("alice", 10)))
            
            assertThat(response, cast(has(RoutedResponse::xUriTemplate, equalTo(
                UriTemplate.from("/example/alice/{x}")))))
        }
        
        class TestFilter : Filter {
            var wasApplied = false
            override fun invoke(next: HttpHandler): HttpHandler {
                return { rq ->
                    wasApplied = true
                    next(rq)
                }
            }
        }
        
        test("can apply a filter to all resources`") {
            val filter = TestFilter()
            
            val filteredApp = app.withFilter(filter)
            
            val response = filteredApp(Request(GET, examplePath.path("alice", 10)))
    
            assertTrue(filter.wasApplied, "filter should have been applied")
    
            assertThat(response, cast(has(RoutedResponse::xUriTemplate, equalTo(
                UriTemplate.from("/example/alice/{x}")))))
        }
        
        test("filters are additive`") {
            val filter1 = TestFilter()
            val filter2 = TestFilter()
            
            val filteredApp = app.withFilter(filter1).withFilter(filter2)
            
            val response = filteredApp(Request(GET, examplePath.path("alice", 10)))
    
            assertTrue(filter1.wasApplied, "filter 1 should have been applied")
            assertTrue(filter2.wasApplied, "filter 2 should have been applied")
    
            assertThat(response, cast(has(RoutedResponse::xUriTemplate, equalTo(
                UriTemplate.from("/example/alice/{x}")))))
        }
    }
}
