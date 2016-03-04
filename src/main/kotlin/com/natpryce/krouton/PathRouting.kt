package com.natpryce.krouton

import com.google.common.net.UrlEscapers
import java.net.URI
import java.util.*


interface UrlScheme<T> {
    fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>?
    fun pathElementsFrom(value: T): List<String>
}

fun <T> UrlScheme<T>.parse(s: String): T? {
    return parse(splitPath(s))
}

fun <T> UrlScheme<T>.parse(splitPath: List<String>): T? {
    return parsePathElements(splitPath)?.let {
        val (result, unused) = it
        if (unused.isEmpty()) result else null
    }
}

fun <T> UrlScheme<T>.path(value: T): String {
    val pathElements = pathElementsFrom(value)
    return joinPath(pathElements)
}

fun <T,U> UrlScheme<Pair<T,U>>.path(t : T, u: U) : String = path(t to u)
fun <T,U,V> UrlScheme<Pair<Pair<T,U>,V>>.path(t : T, u: U, v: V) : String = path(t to u to v)
fun <T,U,V,W> UrlScheme<Pair<Pair<Pair<T,U>,V>,W>>.path(t : T, u: U, v: V, w: W) : String = path(t to u to v to w)


private fun decodePathElement(s: String) : String = URI(s).path

private fun encodePathElement(s: String) : String = UrlEscapers.urlPathSegmentEscaper().escape(s)

fun splitPath(path: String) = path.split("/").filterNot(String::isEmpty).map(::decodePathElement)

fun joinPath(pathElements: List<String>) = "/" + pathElements.map(::encodePathElement).joinToString("/")



object root : UrlScheme<Unit> {
    override fun pathElementsFrom(value: Unit) = emptyList<String>()

    override fun parsePathElements(pathElements: List<String>) = Pair(Unit, pathElements)
}


abstract class PathElement<T> : UrlScheme<T> {
    override fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>? =
            pathElements.firstOrNull()
                    ?.let { parsePathElement(it) }
                    ?.let { it to pathElements.tail() }

    override fun pathElementsFrom(value: T): List<String> =
            listOf(pathElementFrom(value))

    abstract fun parsePathElement(element: String): T?

    open fun pathElementFrom(value: T): String = value.toString()
}

class PrefixedUrlScheme<T>(private val prefix: String, private val rest: UrlScheme<T>) : UrlScheme<T> {
    override fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>? {
        if (pathElements.size > 1 && pathElements[0] == prefix) {
            return rest.parsePathElements(pathElements.tail())
        } else {
            return null
        }
    }

    override fun pathElementsFrom(value: T) =
            listOf(prefix) + rest.pathElementsFrom(value)
}

class SuffixedUrlScheme<T>(private val rest: UrlScheme<T>, private val suffix: String) : UrlScheme<T> {
    override fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>? {
        val parse = rest.parsePathElements(pathElements)

        if (parse == null || parse.second.firstOrNull() != suffix) {
            return null
        } else {
            return Pair(parse.first, parse.second.tail())
        }
    }

    override fun pathElementsFrom(value: T) =
            rest.pathElementsFrom(value) + listOf(suffix)
}


class AppendedUrlScheme<T,U>(private val tScheme: UrlScheme<T>, private val uScheme: UrlScheme<U>) : UrlScheme<Pair<T,U>> {
    override fun parsePathElements(pathElements: List<String>): Pair<Pair<T, U>, List<String>>? {
        return tScheme.parsePathElements(pathElements)?.let { tParse ->
            val (t, uPathElements) = tParse
            uScheme.parsePathElements(uPathElements)?.let { uParse ->
                val (u, rest) = uParse

                Pair(t,u) to rest
            }
        }
    }

    override fun pathElementsFrom(value: Pair<T, U>): List<String> {
        return tScheme.pathElementsFrom(value.first) + uScheme.pathElementsFrom(value.second)
    }
}

class RestrictedUrlScheme<T>(private val base: UrlScheme<T>, private val p: (T) -> Boolean) : UrlScheme<T> {
    override fun parsePathElements(pathElements: List<String>) =
            base.parsePathElements(pathElements)?.flatMapFirst { if (p(it)) it else null }

    override fun pathElementsFrom(value: T) = base.pathElementsFrom(value)
}

class RepeatedUrlScheme<T>(private val elementScheme: UrlScheme<T>) : UrlScheme<List<T>> {
    override fun parsePathElements(pathElements: List<String>) = parse(ArrayList<T>(), pathElements)

    override fun pathElementsFrom(value: List<T>) = value.flatMap { elementScheme.pathElementsFrom(it) }

    private tailrec fun parse(accumulator: MutableList<T>, pathElements: List<String>) : Pair<List<T>,List<String>> {
        val parsed = elementScheme.parsePathElements(pathElements)
        return when(parsed) {
            null -> Pair(accumulator, pathElements)
            else -> { val (element, rest) = parsed
                accumulator.add(element)
                parse(accumulator, rest)
            }
        }
    }
}


interface Projection1<T,U> {
    fun fromParts(t: T): U?
    fun toParts(u: U): T
}

class Projection1UrlScheme<T1, U>(
        private val base: UrlScheme<T1>,
        private val projection: Projection1<T1, U>) : UrlScheme<U>
{
    override fun parsePathElements(pathElements: List<String>) =
            base.parsePathElements(pathElements).flatMapFirst { projection.fromParts(it) }

    override fun pathElementsFrom(value: U) =
            base.pathElementsFrom(projection.toParts(value))
}

interface Projection2<T1, T2, U> {
    fun fromParts(t1: T1, t2: T2): U?
    fun toParts(u: U): Pair<T1, T2>
}

class Projection2UrlScheme<T1, T2, U>(
        private val base: UrlScheme<Pair<T1, T2>>,
        private val projection: Projection2<T1, T2, U>) : UrlScheme<U>
{
    override fun parsePathElements(pathElements: List<String>) =
            base.parsePathElements(pathElements).flatMapFirst { projection.fromParts(it.first, it.second) }

    override fun pathElementsFrom(value: U) =
            base.pathElementsFrom(projection.toParts(value))
}

interface Projection3<T1, T2, T3, U> {
    fun fromParts(t1: T1, t2: T2, t3: T3): U?
    fun toParts(u: U): Pair<Pair<T1, T2>, T3>
}

class Projection3UrlScheme<T1, T2, T3, U>(
        private val base: UrlScheme<Pair<Pair<T1, T2>, T3>>,
        private val projection: Projection3<T1, T2, T3, U>) : UrlScheme<U>
{
    override fun parsePathElements(pathElements: List<String>) =
            base.parsePathElements(pathElements).flatMapFirst {
                projection.fromParts(it.first.first, it.first.second, it.second) }

    override fun pathElementsFrom(value: U) =
            base.pathElementsFrom(projection.toParts(value))
}

interface Projection4<T1, T2, T3, T4, U> {
    fun fromParts(t1: T1, t2: T2, t3: T3, t4: T4): U?
    fun toParts(u: U): Pair<Pair<Pair<T1, T2>, T3>, T4>
}

class Projection4UrlScheme<T1, T2, T3, T4, U>(
        private val base: UrlScheme<Pair<Pair<Pair<T1, T2>, T3>, T4>>,
        private val projection: Projection4<T1, T2, T3, T4, U>) : UrlScheme<U>
{
    override fun parsePathElements(pathElements: List<String>) =
            base.parsePathElements(pathElements).flatMapFirst {
                projection.fromParts(it.first.first.first, it.first.first.second, it.first.second, it.second) }

    override fun pathElementsFrom(value: U) =
            base.pathElementsFrom(projection.toParts(value))
}
