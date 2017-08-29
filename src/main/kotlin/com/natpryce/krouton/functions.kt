package com.natpryce.krouton

internal fun <T> List<T>.tail(n: Int = 1): List<T> = subList(n, size)

internal fun <T1,T2,U> Pair<T1,U>?.flatMapFirst(f: (T1)->T2?): Pair<T2,U>? = this?.let{f(first)?.let{it to second}}

internal inline fun <T, reified X : Exception> parse(s: String, parser: (String) -> T): T? =
        try {
            parser(s)
        } catch (e: Exception) {
            if (e is X) {
                null
            } else {
                throw e
            }
        }
