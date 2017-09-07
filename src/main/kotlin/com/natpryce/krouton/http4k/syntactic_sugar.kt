package com.natpryce.krouton.http4k

import com.natpryce.krouton.Empty
import com.natpryce.krouton.PathTemplate
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status


class ResourceRoutesBuilder(private val monitor: RequestMonitor?) {
    private val routes = mutableListOf<PathMatchingHttpHandler<*>>()
    private var handlerIfNoMatch: HttpHandler = { Response(Status.NOT_FOUND) }
    
    operator fun <T> PathTemplate<T>.invoke(handler: (Request, T) -> Response) {
        addPathHandler(this, handler)
    }
    
    operator fun PathTemplate<Empty>.invoke(handler: (Request) -> Response) {
        addPathHandler(this, emptyHandler(handler))
    }
    
    infix fun <T> PathTemplate<T>.methods(block: MethodRoutesBuilder<T>.() -> Unit) {
        addPathHandler(this, MethodRoutesBuilder<T>().apply(block).toHandler())
    }
    
    @JvmName("methodsEmpty")
    infix fun PathTemplate<Empty>.methods(block: MethodRoutesBuilderEmpty.() -> Unit) {
        addPathHandler(this, MethodRoutesBuilderEmpty().apply(block).toHandler())
    }
    
    fun otherwise(handler: HttpHandler) {
        handlerIfNoMatch = handler
    }
    
    protected fun <T> addPathHandler(pathTemplate: PathTemplate<T>, handler: Request.(T) -> Response) {
        routes.add(PathMatchingHttpHandler(pathTemplate, handler, monitor))
    }
    
    internal fun toHandler() =
        PathRouter(routes.toList(), handlerIfNoMatch)
}

class MethodRoutesBuilder<T> {
    private val routes = mutableListOf<(Request, T) -> Response?>()
    private var handlerIfNoMatch: (Request, T) -> Response = { _, _ -> Response(Status.METHOD_NOT_ALLOWED) }
    
    operator fun Method.invoke(handler: (Request, T) -> Response) {
        val requiredMethod = this
        routes += methodHandler(requiredMethod, handler)
    }
    
    fun otherwise(handler: (Request, T) -> Response) {
        handlerIfNoMatch = handler
    }
    
    internal fun toHandler() =
        router(routes, handlerIfNoMatch)
}

class MethodRoutesBuilderEmpty {
    private val routes = mutableListOf<(Request,Empty) -> Response?>()
    private var handlerIfNoMatch: (Request) -> Response = { Response(Status.METHOD_NOT_ALLOWED) }
    
    operator fun Method.invoke(handler: (Request) -> Response) {
        val requiredMethod = this
        routes += methodHandler(requiredMethod, emptyHandler(handler))
    }
    
    fun otherwise(handler: (Request) -> Response) {
        handlerIfNoMatch = handler
    }
    
    internal fun toHandler() =
        router(routes, emptyHandler(handlerIfNoMatch))
}

private fun emptyHandler(handler: (Request) -> Response) = { r: Request, _: Empty -> handler(r) }


fun resources(setup: ResourceRoutesBuilder.() -> Unit) =
    ResourceRoutesBuilder(null).apply(setup).toHandler()

fun resources(monitor: RequestMonitor, setup: ResourceRoutesBuilder.() -> Unit) =
    ResourceRoutesBuilder(monitor).apply(setup).toHandler()
