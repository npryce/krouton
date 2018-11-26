package com.natpryce.krouton.example

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import kotlin.test.fail


internal fun HttpHandler.getText(path: String): String {
    val response = this.get(path)
    return when {
        response.status.successful -> response.bodyString()
        else -> fail("request failed, status: ${response.status}")
    }
}

internal fun HttpHandler.get(path: String): Response {
    Request(GET, path)
    val response = this(Request(GET, path))
    return when {
        response.status.redirection -> this@get.get(response.header("location") ?: fail("no redirect location"))
        else -> response
    }
}
