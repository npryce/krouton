package com.natpryce.krouton.http4k

import com.natpryce.krouton.PathTemplate
import com.natpryce.krouton.monitoredPath
import com.natpryce.krouton.parse
import com.natpryce.krouton.splitPath
import com.natpryce.krouton.toUrlTemplate
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

typealias Route<T> = (Request, T) -> Response?

interface ReportsUrlTemplates {
    fun urlTemplates(): List<String>
}

typealias RequestMonitor = (Request, Response, String) -> Unit

interface Monitored<out T : Monitored<T>> {
    fun withMonitor(monitor: RequestMonitor): T
}

data class Router<in T, out ROUTE : Route<T>>(
    val routes: List<ROUTE>,
    val handlerIfNoMatch: (Request, T) -> Response

) : (Request, T) -> Response {
    
    override fun invoke(request: Request, t: T): Response =
        routes.firstNonNull { it(request, t) } ?: handlerIfNoMatch(request, t)
}


fun <T, ROUTE> Router<T, ROUTE>.withMonitor(monitor: RequestMonitor)
    where ROUTE : Route<T>,
          ROUTE : Monitored<ROUTE> =
    copy(routes = routes.map { it.withMonitor(monitor) })


fun <T, ROUTE> Router<T, ROUTE>.urlTemplates()
    where ROUTE : Route<T>,
          ROUTE : ReportsUrlTemplates =
    routes.flatMap { it.urlTemplates() }


interface ResourceRoute : Route<List<String>>, ReportsUrlTemplates, Monitored<ResourceRoute>


data class ResourceRouter(val router: Router<List<String>, ResourceRoute>) :
    HttpHandler,
    ReportsUrlTemplates,
    Monitored<ResourceRouter>
{
    constructor(routes: List<ResourceRoute>, handlerIfNoMatch: HttpHandler) :
        this(Router(routes, { rq, _ -> handlerIfNoMatch(rq) }))
    
    override fun invoke(request: Request) =
        router(request, splitPath(request.uri.path))
    
    override fun urlTemplates() =
        router.urlTemplates()
    
    override fun withMonitor(monitor: RequestMonitor) =
        copy(router = router.withMonitor(monitor))
}

data class PathParsingRoute<T>(
    private val pathTemplate: PathTemplate<T>,
    private val handler: (Request, T) -> Response,
    private val monitor: RequestMonitor?
) : ResourceRoute {
    
    override fun invoke(request: Request, path: List<String>): Response? =
        pathTemplate.parse(path)?.let { parsed ->
            handler(request, parsed)
                .also { monitor?.invoke(request, it, pathTemplate.monitoredPath(parsed)) }
        }
    
    override fun urlTemplates() = listOf(pathTemplate.toUrlTemplate())
    
    override fun withMonitor(monitor: RequestMonitor) = copy(monitor = monitor)
}

private inline fun <T, U> List<T>.firstNonNull(f: (T) -> U?): U? {
    forEach { t -> f(t)?.let { return it } }
    return null
}

fun <T> methodHandler(requiredMethod: Method, handler: (Request, T) -> Response): (Request, T) -> Response? =
    fun(request: Request, t: T) =
        if (request.method == requiredMethod) handler(request, t) else null
