package com.natpryce.krouton.http4k

import com.natpryce.krouton.PathTemplate
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

interface ResourceRoutesSyntax {
    operator fun <T> PathTemplate<T>.invoke(handler: Request.(T) -> Response)
    infix fun <T> PathTemplate<T>.methods(block: MethodRoutesSyntax<T>.()->Unit)
    fun otherwise(handler: HttpHandler)
}

class ResourceRoutesBuilder(private val monitor: RequestMonitor?) : ResourceRoutesSyntax {
    private val routes = mutableListOf<PathMatchingHttpHandler>()
    private var handlerIfNoMatch : HttpHandler = { Response(Status.NOT_FOUND) }
    
    override operator fun <T> PathTemplate<T>.invoke(handler: Request.(T) -> Response) {
        addPathHandler(this, handler)
    }
    
    override infix fun <T> PathTemplate<T>.methods(block: MethodRoutesSyntax<T>.()->Unit) {
        addPathHandler(this, MethodRoutesBuilder<T>().apply(block).toHandler())
    }
    
    private fun <T> addPathHandler(pathTemplate: PathTemplate<T>, handler: Request.(T) -> Response) {
        routes.add(pathHandler(pathTemplate, handler, monitor))
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
    ResourceRoutesBuilder(null).apply(setup).toHandler()

inline fun resources(noinline monitor: RequestMonitor, setup: ResourceRoutesSyntax.() -> Unit) =
    ResourceRoutesBuilder(monitor).apply(setup).toHandler()
