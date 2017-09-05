package com.natpryce.krouton

interface PathElementType<T> {
    fun parsePathElement(element: String): T?
    fun pathElementFrom(value: T): String = value.toString()
}