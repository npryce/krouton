package com.natpryce.krouton.http4k

import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.firstNonNull
import com.natpryce.krouton.parse
import com.natpryce.krouton.splitPath
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response


fun <T> router(routes: MutableList<Request.(T) -> Response?>, handlerIfNoMatch: Request.(T)->Response) =
    fun Request.(t: T): Response =
        routes.firstNonNull { this.it(t) } ?: this.handlerIfNoMatch(t)


// Would not be needed if Request held the path elements
typealias PathMatchingHttpHandler = (Request, List<String>) -> Response?

fun pathRouter(routes: List<PathMatchingHttpHandler>, handlerIfNoMatch: HttpHandler) : HttpHandler =
    fun(request: Request): Response {
        val pathElements = splitPath(request.uri.path)
        return routes.firstNonNull { it(request, pathElements) } ?: handlerIfNoMatch(request)
    }


fun <T> pathHandler(urlScheme: UrlScheme<T>, handler: Request.(T) -> Response): PathMatchingHttpHandler =
    fun(request: Request, path: List<String>) =
        urlScheme.parse(path)?.let { request.handler(it) }


