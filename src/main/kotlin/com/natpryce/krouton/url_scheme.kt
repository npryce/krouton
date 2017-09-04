package com.natpryce.krouton

import com.google.common.net.UrlEscapers
import java.net.URI
import kotlin.reflect.KProperty

sealed class TemplatePathElement
data class Literal(val value: String) : TemplatePathElement()
data class Variable(val name: String) : TemplatePathElement()



interface PathElementType<T> {
    fun parsePathElement(element: String): T?
    fun pathElementFrom(value: T): String = value.toString()
}



interface UrlScheme<T> {
    fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>?
    fun pathElementsFrom(value: T): List<String>
    fun monitoredPathElementsFrom(value: T): List<TemplatePathElement>
}

fun <T> UrlScheme<T>.parse(s: String): T? {
    return parse(splitPath(s))
}

fun <T> UrlScheme<T>.parse(splitPath: List<String>): T? =
    parsePathElements(splitPath)?.let { (result, unused) -> if (unused.isEmpty()) result else null }

fun <T> UrlScheme<T>.path(value: T): String =
    joinPath(pathElementsFrom(value), transform = ::encodePathElement)

fun <T> UrlScheme<T>.monitoredPath(value: T): String =
    joinPath(monitoredPathElementsFrom(value), ::encodeTemplatePathElement)


private fun decodePathElement(s: String): String =
    URI(s).path

internal fun splitPath(path: String) =
    path.split("/").filterNot(String::isEmpty).map(::decodePathElement)

internal fun <T> joinPath(pathElements: List<T>, transform: (T) -> CharSequence) =
    "/" + pathElements.joinToString("/", transform = transform)

internal fun encodePathElement(s: String): String =
    UrlEscapers.urlPathSegmentEscaper().escape(s)

internal fun encodeTemplatePathElement(it: TemplatePathElement): CharSequence {
    return when (it) {
        is Variable -> "{" + it.name + "}"
        is Literal -> encodePathElement(it.value)
    }
}


object root : UrlScheme<Empty> {
    override fun parsePathElements(pathElements: List<String>) = Pair(Empty, pathElements)
    override fun pathElementsFrom(value: Empty) = emptyList<String>()
    override fun monitoredPathElementsFrom(value: Empty) = emptyList<TemplatePathElement>()
}

abstract class PathElement<T> : UrlScheme<T> {
    final override fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>? =
        pathElements.firstOrNull()
            ?.let { parsePathElement(it) }
            ?.let { it to pathElements.drop(1) }
    
    abstract fun parsePathElement(element: String): T?
    
    final override fun pathElementsFrom(value: T): List<String> =
        listOf(pathElementFrom(value))
    
    abstract fun pathElementFrom(value: T): String
    
    final override fun monitoredPathElementsFrom(value: T): List<TemplatePathElement> =
        listOf(monitoredPathElementFrom(value))
    
    abstract fun monitoredPathElementFrom(value: T): TemplatePathElement
}

data class VariablePathElement<T>(val type: PathElementType<T>, val name: String, val monitored: Boolean = false) : PathElement<T>() {
    override fun parsePathElement(element: String): T? =
        type.parsePathElement(element)
    override fun pathElementFrom(value: T): String =
        type.pathElementFrom(value)
    override fun monitoredPathElementFrom(value: T): TemplatePathElement =
        if (monitored) Literal(pathElementFrom(value)) else Variable(name)
}

fun <T> VariablePathElement<T>.named(name: String) = copy(name = name)

fun <T> VariablePathElement<T>.monitored() = copy(monitored = true)

operator fun <T> PathElementType<T>.getValue(obj: Any?, property: KProperty<*>): VariablePathElement<T> =
    VariablePathElement(this, property.name)


operator fun <T> VariablePathElement<T>.getValue(obj: Any?, property: KProperty<*>): VariablePathElement<T> =
    copy(name = property.name)



class FixedPathElement(private val pathElement: String) : PathElement<Empty>() {
    override fun parsePathElement(element: String) = Empty.takeIf { element == pathElement }
    override fun pathElementFrom(value: Empty) = pathElement
    override fun monitoredPathElementFrom(value: Empty) = Literal(pathElement)
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
    
    override fun monitoredPathElementsFrom(value: HCons<U, T>) =
        tScheme.monitoredPathElementsFrom(value.rest) + uScheme.monitoredPathElementsFrom(value.top)
}

class PrefixedUrlScheme<T>(private val prefix: UrlScheme<Empty>, private val rest: UrlScheme<T>) : UrlScheme<T> {
    override fun pathElementsFrom(value: T) =
        prefix.pathElementsFrom(Empty) + rest.pathElementsFrom(value)
    
    override fun parsePathElements(pathElements: List<String>) =
        prefix.parsePathElements(pathElements)
            ?.let { (_, restPathElements) -> rest.parsePathElements(restPathElements) }
    
    override fun monitoredPathElementsFrom(value: T) =
        prefix.monitoredPathElementsFrom(Empty) + rest.monitoredPathElementsFrom(value)
}


class SuffixedUrlScheme<T>(private val first: UrlScheme<T>, private val suffix: UrlScheme<Empty>) : UrlScheme<T> {
    override fun pathElementsFrom(value: T) =
        first.pathElementsFrom(value) + suffix.pathElementsFrom(Empty)
    
    override fun parsePathElements(pathElements: List<String>) =
        first.parsePathElements(pathElements)
            ?.let { (value, restPathElements) ->
                suffix.parsePathElements(restPathElements)
                    ?.let { (_, remainderPathElements) -> Pair(value, remainderPathElements) }
            }
    
    override fun monitoredPathElementsFrom(value: T) =
        first.monitoredPathElementsFrom(value) + suffix.monitoredPathElementsFrom(Empty)
}

class RestrictedUrlScheme<T>(private val base: UrlScheme<T>, private val p: (T) -> Boolean) : UrlScheme<T> {
    override fun parsePathElements(pathElements: List<String>) =
        base.parsePathElements(pathElements)?.flatMapFirst { if (p(it)) it else null }
    
    override fun pathElementsFrom(value: T) = base.pathElementsFrom(value)
    
    override fun monitoredPathElementsFrom(value: T) = base.monitoredPathElementsFrom(value)
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
    
    override fun monitoredPathElementsFrom(value: U) =
        base.monitoredPathElementsFrom(projection.toParts(value))
}
