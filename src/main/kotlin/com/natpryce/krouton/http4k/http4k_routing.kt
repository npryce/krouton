package com.natpryce.krouton.http4k

import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.firstNonNull
import com.natpryce.krouton.parse
import com.natpryce.krouton.splitPath
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.METHOD_NOT_ALLOWED
import org.http4k.core.Status.Companion.NOT_FOUND



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


interface ResourceRoutesSyntax {
    operator fun <T> UrlScheme<T>.invoke(handler: Request.(T) -> Response)
    infix fun <T> UrlScheme<T>.by(block: MethodRoutesSyntax<T>.()->Unit)
    fun otherwise(handler: HttpHandler)
}

class ResourceRoutesBuilder : ResourceRoutesSyntax {
    private val routes = mutableListOf<PathMatchingHttpHandler>()
    private var handlerIfNoMatch : HttpHandler = { Response(NOT_FOUND) }
    
    override operator fun <T> UrlScheme<T>.invoke(handler: Request.(T) -> Response) {
        routes.add(pathHandler(this, handler))
    }
    
    override infix fun <T> UrlScheme<T>.by(block: MethodRoutesSyntax<T>.()->Unit) {
        routes.add(pathHandler(this, MethodRoutesBuilder<T>().apply(block).toHandler()))
    }
    
    override fun otherwise(handler: HttpHandler) {
        handlerIfNoMatch = handler
    }
    
    fun toHandler() = pathRouter(routes.toList(), handlerIfNoMatch)
}


interface MethodRoutesSyntax<out T> {
    operator fun Method.invoke(handler: Request.(T)->Response)
    fun otherwise(handler: Request.(T) -> Response)
}

class MethodRoutesBuilder<T> : MethodRoutesSyntax<T> {
    private val routes = mutableListOf<Request.(T)->Response?>()
    private var handlerIfNoMatch : Request.(T)->Response = { Response(METHOD_NOT_ALLOWED) }
    
    override fun Method.invoke(handler: Request.(T) -> Response) {
        val requiredMethod = this
        routes += { t -> if (method == requiredMethod) this.handler(t) else null }
    }
    
    override fun otherwise(handler: Request.(T)->Response) {
        handlerIfNoMatch = handler
    }
    
    fun toHandler() =
        router(routes, handlerIfNoMatch)
    
}

fun <T> router(routes: MutableList<Request.(T) -> Response?>, handlerIfNoMatch: Request.(T)->Response) =
    fun Request.(t: T): Response =
        routes.firstNonNull { this.it(t) } ?: this.handlerIfNoMatch(t)

inline fun resources(setup: ResourceRoutesSyntax.() -> Unit) =
    ResourceRoutesBuilder().apply(setup).toHandler()
