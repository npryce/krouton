package com.natpryce.krouton.http4k


operator fun ResourceRouter.plus(that: ResourceRouter) =
    ResourceRouter(this.router + that.router)

operator fun <T, ROUTE: Route<T>> Router<T,ROUTE>.plus(that: Router<T,ROUTE>) =
    Router(this.routes + that.routes, that.handlerIfNoMatch)
