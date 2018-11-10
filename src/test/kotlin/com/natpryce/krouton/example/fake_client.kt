package com.natpryce.krouton.example

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import kotlin.test.fail


internal fun getText(service: HttpHandler, path: String): String {
    val response = get(service, path)
    return when {
        response.status.successful -> response.bodyString()
        else -> fail("request failed, status: ${response.status}")
    }
}

internal fun get(service: HttpHandler, path: kotlin.String): Response {
    Request(GET, path)
    val response = service(Request(GET, path))
    return when {
        response.status.redirection -> get(service, response.header("location") ?: fail("no redirect location"))
        else -> response
    }
}
