package com.natpryce.krouton

infix fun <EXCH, T> UrlScheme<T>.by(handler: (EXCH, T) -> Unit) = fun(exchange: EXCH, path: String) : Boolean {
    val parsed = parse(path)
    if (parsed == null) {
        return false
    }
    else {
        handler(exchange, parsed)
        return true
    }
}


fun <EXCH, T> routeBy(exchangeToCriteria: (EXCH) -> T, vararg routes: (EXCH, T) -> Boolean) = fun(exchange: EXCH): Boolean {
    val criteria = exchangeToCriteria(exchange)
    for (route in routes) {
        if (route(exchange, criteria)) return true
    }

    return false
}


infix fun <EXCH> ((EXCH) -> Boolean).otherwise(fallback: (EXCH) -> Unit): (EXCH) -> Unit = fun(exchange: EXCH) {
    if (!this(exchange)) fallback(exchange)
}
