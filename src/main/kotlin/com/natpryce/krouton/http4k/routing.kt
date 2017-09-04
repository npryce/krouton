package com.natpryce.krouton.http4k

import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.firstNonNull
import com.natpryce.krouton.monitoredPath
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

typealias RequestMonitor = (Request, Response, String)->Unit

fun pathRouter(routes: List<PathMatchingHttpHandler>, handlerIfNoMatch: HttpHandler) : HttpHandler =
    fun(request: Request): Response {
        val pathElements = splitPath(request.uri.path)
        return routes.firstNonNull { it(request, pathElements) } ?: handlerIfNoMatch(request)
    }


fun <T> pathHandler(urlScheme: UrlScheme<T>, handler: Request.(T) -> Response, monitor: RequestMonitor?): PathMatchingHttpHandler =
    fun(request: Request, path: List<String>): Response? {
        val parsed = urlScheme.parse(path)
        if (parsed != null) {
            val response = request.handler(parsed)
            if (monitor != null) {
                monitor(request, response, urlScheme.monitoredPath(parsed))
            }
            return response
        }
        else {
            return null
        }
    }



