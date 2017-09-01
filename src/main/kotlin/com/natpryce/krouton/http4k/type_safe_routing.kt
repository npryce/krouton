package com.natpryce.krouton.http4k

import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.parse
import com.natpryce.krouton.splitPath
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND


private inline fun <T,U> List<T>.firstNonNull(f: (T)->U?): U? {
    forEach { t -> f(t)?.let { return it } }
    return null
}

typealias PathMatchingHttpHandler = (request: Request, path: List<String>) -> Response?

fun pathRouter(routes: List<PathMatchingHttpHandler>, handlerIfNoMatch: HttpHandler = {Response(NOT_FOUND)}): HttpHandler =
    fun(request: Request): Response {
        val splitPath = splitPath(request.uri.path)
        return routes.firstNonNull { it(request, splitPath) } ?: handlerIfNoMatch(request)
    }

infix fun <T> UrlScheme<T>.by(handler: Request.(pathElements: T)-> Response): PathMatchingHttpHandler =
    fun(request: Request, path: List<String>) =
        this.parse(path)?.let { request.handler(it) }
