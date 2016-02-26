package com.natpryce.krouton.reactive

import com.natpryce.krouton.UrlScheme
import com.natpryce.krouton.parse

infix fun <Exchange, T> UrlScheme<T>.by(handler: (Exchange, T) -> Unit) = fun(exchange: Exchange, path: String): Boolean {
    val parsed = parse(path)
    if (parsed == null) {
        return false
    } else {
        handler(exchange, parsed)
        return true
    }
}

fun <Exchange, Criteria> routerBy(exchangeToCriteria: (Exchange) -> Criteria,
                                  vararg routes: (Exchange, Criteria) -> Boolean) =
        fun(exchange: Exchange): Boolean =
                routeBy(exchange, exchangeToCriteria(exchange), *routes)

fun <Exchange, Criteria> routeBy(exchange: Exchange,
                                 criteria: Criteria,
                                 vararg routes: (Exchange, Criteria) -> Boolean): Boolean {
    for (route in routes) {
        if (route(exchange, criteria)) return true
    }
    return false
}


infix fun <Exchange, Criteria> ((Exchange, Criteria) -> Boolean).or(that: (Exchange, Criteria) -> Boolean) =
        fun(exchange: Exchange, criteria: Criteria) = this(exchange, criteria) || that(exchange, criteria)

infix fun <Exchange> ((Exchange) -> Boolean).otherwise(fallback: (Exchange) -> Unit): (Exchange) -> Unit = fun(exchange: Exchange) {
    if (!this(exchange)) fallback(exchange)
}
