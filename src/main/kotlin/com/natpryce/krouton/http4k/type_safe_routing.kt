package com.natpryce.krouton.http4k

import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.parse
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response

typealias MatchingHttpHandler = (Request) -> Response?

fun routingHttpHandler(routes: List<MatchingHttpHandler>, handlerIfNoMatch: HttpHandler): HttpHandler =
    fun(rq: Request): Response {
        routes.forEach { request -> request(rq)?.let { response -> return response } }
        return handlerIfNoMatch(rq)
    }

infix fun <T> UrlScheme<T>.by(handler: Request.(pathElements: T)-> Response): MatchingHttpHandler =
    fun(rq: Request) = this.parse(rq.uri.path)?.let { rq.handler(it) }
