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

/**
 * A route might map a request and some data parsed from that request to a response.
 */
typealias Route<T> = (Request, T) -> Response?

/**
 * The capability to describe its routing as URL templates (a subset of RFC 6570)
 */
interface ReportsUrlTemplates {
    fun urlTemplates(): List<String>
}

/**
 * An event listener that reports a request & response, and the URL template that matched it
 */
typealias RequestMonitor = (request: Request, response: Response, urlTemplate: String) -> Unit

/**
 * The capability to be monitored by a RequestMonitor
 */
interface Monitored<out T : Monitored<T>> {
    fun withMonitor(monitor: RequestMonitor): T
}

/**
 *  A Krouton handler that dispatches to the first element of the `routes` that matches the request,
 *  and invokes `handlerIfNoMatch` if none of them match.
 */
data class Router<in T, out ROUTE : Route<T>>(
    val routes: List<ROUTE>,
    val handlerIfNoMatch: (Request, T) -> Response

) : (Request, T) -> Response {
    
    override fun invoke(request: Request, t: T): Response =
        routes.firstNonNull { it(request, t) } ?: handlerIfNoMatch(request, t)
}


/**
 * Attaches a monitor to all `routes` if they can be monitored
 */
fun <T, ROUTE> Router<T, ROUTE>.withMonitor(monitor: RequestMonitor)
    where ROUTE : Route<T>,
          ROUTE : Monitored<ROUTE> =
    copy(routes = routes.map { it.withMonitor(monitor) })


/**
 * Returns the URL templates of all `routes` if they can report their routing rule as a URL template
 */
fun <T, ROUTE> Router<T, ROUTE>.urlTemplates()
    where ROUTE : Route<T>,
          ROUTE : ReportsUrlTemplates =
    routes.flatMap { it.urlTemplates() }


/**
 * A route for one resource, identified by a URL template, which can be monitored.
 */
interface ResourceRoute : Route<List<String>>, ReportsUrlTemplates, Monitored<ResourceRoute>

/**
 * A ResourceRouter is an HttpHandler that can route the request to one of its ResourceRoutes
 */
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

/**
 * A ResourceRoute that uses Krouton PathTemplates to match paths.
 */
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
