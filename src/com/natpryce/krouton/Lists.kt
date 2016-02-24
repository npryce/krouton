package com.natpryce.krouton

internal fun <T> List<T>.tail(n: Int = 1): List<T> = subList(n, size)
