package com.natpryce.krouton

interface Projection<Parts, Mapped> {
    fun fromParts(parts: Parts): Mapped?
    fun toParts(mapped: Mapped): Parts
}

fun <Parts, Mapped> projection(fromParts: (Parts) -> Mapped?, toParts: (Mapped) -> Parts) = object : Projection<Parts, Mapped> {
    override fun fromParts(parts: Parts) = fromParts.invoke(parts)
    override fun toParts(mapped: Mapped) = toParts.invoke(mapped)
}
