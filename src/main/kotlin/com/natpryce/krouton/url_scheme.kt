package com.natpryce.krouton

import com.google.common.net.UrlEscapers
import java.net.URI


interface UrlScheme<T> {
    fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>?
    fun pathElementsFrom(value: T): List<String>
}

fun <T> UrlScheme<T>.parse(s: String): T? {
    return parse(splitPath(s))
}

fun <T> UrlScheme<T>.parse(splitPath: List<String>): T? =
    parsePathElements(splitPath)?.let { (result, unused) -> if (unused.isEmpty()) result else null }

fun <T> UrlScheme<T>.path(value: T): String =
    joinPath(pathElementsFrom(value))

@JvmName("pathHStack1")
fun <A> UrlScheme<HStack1<A>>.path(a: A) =
    path(Empty + a)

@JvmName("pathHStack2")
fun <A, B> UrlScheme<HStack2<B, A>>.path(a: A, b: B) =
    path(Empty + a + b)

@JvmName("pathHStack3")
fun <A, B, C> UrlScheme<HStack3<C, B, A>>.path(a: A, b: B, c: C) =
    path(Empty + a + b + c)

@JvmName("pathHStack4")
fun <A, B, C, D> UrlScheme<HStack4<D, C, B, A>>.path(a: A, b: B, c: C, d: D) =
    path(Empty + a + b + c + d)

@JvmName("pathHStack5")
fun <A, B, C, D, E> UrlScheme<HStack5<E, D, C, B, A>>.path(a: A, b: B, c: C, d: D, e: E) =
    path(Empty + a + b + c + d + e)

@JvmName("pathHStack6")
fun <A, B, C, D, E, F> UrlScheme<HStack6<F, E, D, C, B, A>>.path(a: A, b: B, c: C, d: D, e: E, f: F) =
    path(Empty + a + b + c + d + e + f)

private fun decodePathElement(s: String): String =
    URI(s).path

private fun encodePathElement(s: String): String =
    UrlEscapers.urlPathSegmentEscaper().escape(s)

internal fun splitPath(path: String) =
    path.split("/").filterNot(String::isEmpty).map(::decodePathElement)

internal fun joinPath(pathElements: List<String>) =
    "/" + pathElements.joinToString("/", transform = ::encodePathElement)


object root : UrlScheme<Empty> {
    override fun pathElementsFrom(value: Empty) = emptyList<String>()
    override fun parsePathElements(pathElements: List<String>) = Pair(Empty, pathElements)
}

abstract class PathElement<T> : UrlScheme<T> {
    final override fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>? =
        pathElements.firstOrNull()
            ?.let { parsePathElement(it) }
            ?.let { it to pathElements.drop(1) }
    
    abstract fun parsePathElement(element: String): T?
    
    final override fun pathElementsFrom(value: T): List<String> =
        listOf(pathElementFrom(value))
    
    open fun pathElementFrom(value: T): String = value.toString()
}

class FixedPathElement(private val pathElement: String) : PathElement<Empty>() {
    override fun parsePathElement(element: String) = Empty.takeIf { element == pathElement }
    override fun pathElementFrom(value: Empty) = pathElement
}

class AppendedUrlScheme<T : HStack, U>(private val tScheme: UrlScheme<T>, private val uScheme: UrlScheme<U>) : UrlScheme<HCons<U, T>> {
    override fun parsePathElements(pathElements: List<String>): Pair<HCons<U, T>, List<String>>? {
        return tScheme.parsePathElements(pathElements)?.let { (t, uPathElements) ->
            uScheme.parsePathElements(uPathElements)?.let { (u, rest) -> (t + u) to rest }
        }
    }
    
    override fun pathElementsFrom(value: HCons<U, T>): List<String> {
        return tScheme.pathElementsFrom(value.rest) + uScheme.pathElementsFrom(value.top)
    }
}

class RestrictedUrlScheme<T>(private val base: UrlScheme<T>, private val p: (T) -> Boolean) : UrlScheme<T> {
    override fun parsePathElements(pathElements: List<String>) =
        base.parsePathElements(pathElements)?.flatMapFirst { if (p(it)) it else null }
    
    override fun pathElementsFrom(value: T) = base.pathElementsFrom(value)
}

class RepeatedUrlScheme<T>(private val elementScheme: UrlScheme<T>) : UrlScheme<List<T>> {
    override fun parsePathElements(pathElements: List<String>) = parse(mutableListOf(), pathElements)
    
    override fun pathElementsFrom(value: List<T>) = value.flatMap { elementScheme.pathElementsFrom(it) }
    
    private tailrec fun parse(accumulator: MutableList<T>, pathElements: List<String>): Pair<List<T>, List<String>> {
        val parsed = elementScheme.parsePathElements(pathElements)
        return when (parsed) {
            null -> Pair(accumulator, pathElements)
            else -> {
                val (element, rest) = parsed
                accumulator.add(element)
                parse(accumulator, rest)
            }
        }
    }
}

interface Projection<Parts, Mapped> {
    fun fromParts(parts: Parts): Mapped?
    fun toParts(mapped: Mapped): Parts
}

fun <Parts, Mapped> projection(fromParts: (Parts) -> Mapped?, toParts: (Mapped) -> Parts) = object : Projection<Parts, Mapped> {
    override fun fromParts(parts: Parts) = fromParts.invoke(parts)
    override fun toParts(mapped: Mapped) = toParts.invoke(mapped)
}

fun <T> tStack(): Projection<T, HStack1<T>> = projection(
    fromParts = { t -> Empty + t },
    toParts = { (t) -> t }
)

class ProjectionUrlScheme<T, U>(
    private val base: UrlScheme<T>,
    private val projection: Projection<T, U>
) : UrlScheme<U> {
    override fun parsePathElements(pathElements: List<String>) =
        base.parsePathElements(pathElements).flatMapFirst { projection.fromParts(it) }
    
    override fun pathElementsFrom(value: U) =
        base.pathElementsFrom(projection.toParts(value))
}
