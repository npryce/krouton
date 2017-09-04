package com.natpryce.krouton.http4k

import com.natpryce.krouton.UrlScheme
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

interface ResourceRoutesSyntax {
    operator fun <T> UrlScheme<T>.invoke(handler: Request.(T) -> Response)
    infix fun <T> UrlScheme<T>.methods(block: MethodRoutesSyntax<T>.()->Unit)
    fun otherwise(handler: HttpHandler)
}

class ResourceRoutesBuilder : ResourceRoutesSyntax {
    private val routes = mutableListOf<PathMatchingHttpHandler>()
    private var handlerIfNoMatch : HttpHandler = { Response(Status.NOT_FOUND) }
    
    override operator fun <T> UrlScheme<T>.invoke(handler: Request.(T) -> Response) {
        routes.add(pathHandler(this, handler))
    }
    
    override infix fun <T> UrlScheme<T>.methods(block: MethodRoutesSyntax<T>.()->Unit) {
        routes.add(pathHandler(this, MethodRoutesBuilder<T>().apply(block).toHandler()))
    }
    
    override fun otherwise(handler: HttpHandler) {
        handlerIfNoMatch = handler
    }
    
    fun toHandler() = pathRouter(routes.toList(), handlerIfNoMatch)
}


interface MethodRoutesSyntax<out T> {
    operator fun Method.invoke(handler: Request.(T)-> Response)
    fun otherwise(handler: Request.(T) -> Response)
}

class MethodRoutesBuilder<T> : MethodRoutesSyntax<T> {
    private val routes = mutableListOf<Request.(T)-> Response?>()
    private var handlerIfNoMatch : Request.(T)-> Response = { Response(Status.METHOD_NOT_ALLOWED) }
    
    override fun Method.invoke(handler: Request.(T) -> Response) {
        val requiredMethod = this
        routes += { t -> if (method == requiredMethod) this.handler(t) else null }
    }
    
    override fun otherwise(handler: Request.(T)-> Response) {
        handlerIfNoMatch = handler
    }
    
    fun toHandler() =
        router(routes, handlerIfNoMatch)
    
}

inline fun resources(setup: ResourceRoutesSyntax.() -> Unit) =
    ResourceRoutesBuilder().apply(setup).toHandler()
