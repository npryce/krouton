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
import org.http4k.core.UriTemplate
import org.http4k.routing.RoutedRequest
import org.http4k.routing.RoutedResponse

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
 * Returns the URL templates of all `routes` if they can report their routing rule as a URL template
 */
fun <T, ROUTE> Router<T, ROUTE>.urlTemplates()
    where ROUTE : Route<T>,
          ROUTE : ReportsUrlTemplates =
    routes.flatMap { it.urlTemplates() }


/**
 * A route for one resource, identified by a URL template, which can be monitored.
 */
interface ResourceRoute : Route<List<String>>, ReportsUrlTemplates

/**
 * A ResourceRouter is an HttpHandler that can route the request to one of its ResourceRoutes
 */
data class ResourceRouter(val router: Router<List<String>, ResourceRoute>) :
    HttpHandler,
    ReportsUrlTemplates
{
    constructor(routes: List<ResourceRoute>, handlerIfNoMatch: HttpHandler) :
        this(Router(routes, { rq, _ -> handlerIfNoMatch(rq) }))
    
    override fun invoke(request: Request): Response {
        return router(request, splitPath(request.uri.path))
    }
    
    override fun urlTemplates() =
        router.urlTemplates()
}

/**
 * A ResourceRoute that uses Krouton PathTemplates to match paths.
 */
data class PathParsingRoute<T>(
    private val pathTemplate: PathTemplate<T>,
    private val handler: (Request, T) -> Response
) : ResourceRoute {
    
    override fun invoke(request: Request, path: List<String>): Response? =
        pathTemplate.parse(path)?.let { parsed ->
            val template = UriTemplate.from(pathTemplate.monitoredPath(parsed))
            
            RoutedResponse(handler(RoutedRequest(request, template), parsed), template)
        }
    
    override fun urlTemplates() = listOf(pathTemplate.toUrlTemplate())
}

private inline fun <T, U> List<T>.firstNonNull(f: (T) -> U?): U? {
    forEach { t -> f(t)?.let { return it } }
    return null
}

fun <T> methodHandler(requiredMethod: Method, handler: (Request, T) -> Response): (Request, T) -> Response? =
    fun(request: Request, t: T) =
        if (request.method == requiredMethod) handler(request, t) else null
