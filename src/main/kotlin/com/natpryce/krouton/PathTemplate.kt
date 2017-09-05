package com.natpryce.krouton

import com.google.common.net.UrlEscapers
import java.net.URI
import kotlin.reflect.KProperty


interface PathTemplate<T> {
    fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>?
    fun pathElementsFrom(value: T): List<String>
    fun monitoredPathElementsFrom(value: T): List<MonitoredPathElement>
}

fun <T> PathTemplate<T>.parse(s: String): T? {
    return parse(splitPath(s))
}

fun <T> PathTemplate<T>.parse(splitPath: List<String>): T? =
    parsePathElements(splitPath)?.let { (result, unused) -> if (unused.isEmpty()) result else null }

fun <T> PathTemplate<T>.path(value: T): String =
    joinPath(pathElementsFrom(value), transform = ::encodePathElement)

fun <T> PathTemplate<T>.monitoredPath(value: T): String =
    joinPath(monitoredPathElementsFrom(value), ::encodeTemplatePathElement)


private fun decodePathElement(s: String): String =
    URI(s).path

internal fun splitPath(path: String) =
    path.split("/").filterNot(String::isEmpty).map(::decodePathElement)

internal fun <T> joinPath(pathElements: List<T>, transform: (T) -> CharSequence) =
    "/" + pathElements.joinToString("/", transform = transform)

internal fun encodePathElement(s: String): String =
    UrlEscapers.urlPathSegmentEscaper().escape(s)

internal fun encodeTemplatePathElement(it: MonitoredPathElement): CharSequence {
    return when (it) {
        is Variable -> "{" + it.name + "}"
        is Literal -> encodePathElement(it.value)
    }
}

/**
 * The root of path, <code>/</code>.
 */
object root : PathTemplate<Empty> {
    override fun parsePathElements(pathElements: List<String>) = Pair(Empty, pathElements)
    override fun pathElementsFrom(value: Empty) = emptyList<String>()
    override fun monitoredPathElementsFrom(value: Empty) = emptyList<MonitoredPathElement>()
}

abstract class PathElement<T> : PathTemplate<T> {
    final override fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>? =
        pathElements.firstOrNull()
            ?.let { parsePathElement(it) }
            ?.let { it to pathElements.drop(1) }
    
    abstract fun parsePathElement(element: String): T?
    
    final override fun pathElementsFrom(value: T): List<String> =
        listOf(pathElementFrom(value))
    
    abstract fun pathElementFrom(value: T): String
    
    final override fun monitoredPathElementsFrom(value: T): List<MonitoredPathElement> =
        listOf(monitoredPathElementFrom(value))
    
    abstract fun monitoredPathElementFrom(value: T): MonitoredPathElement
}

class VariablePathElement<T>(
    private val type: PathElementType<T>,
    private val name: String,
    private val isMonitored: Boolean = false
) : PathElement<T>() {
    override fun parsePathElement(element: String): T? =
        type.parsePathElement(element)
    
    override fun pathElementFrom(value: T): String =
        type.pathElementFrom(value)
    
    override fun monitoredPathElementFrom(value: T): MonitoredPathElement =
        if (isMonitored) Literal(pathElementFrom(value)) else Variable(name)
    
    fun named(name: String) = VariablePathElement(type = type, name = name, isMonitored = isMonitored)
    fun monitored() = VariablePathElement(type = type, name = name, isMonitored = true)
}


operator fun <T> PathElementType<T>.getValue(obj: Any?, property: KProperty<*>): VariablePathElement<T> =
    VariablePathElement(this, property.name)

operator fun <T> VariablePathElement<T>.getValue(obj: Any?, property: KProperty<*>): VariablePathElement<T> =
    this.named(property.name)


class LiteralPathElement(private val pathElement: String) : PathElement<Empty>() {
    override fun parsePathElement(element: String) = Empty.takeIf { element == pathElement }
    override fun pathElementFrom(value: Empty) = pathElement
    override fun monitoredPathElementFrom(value: Empty) = Literal(pathElement)
}

class AppendedPathTemplate<T : HStack, U>(private val tScheme: PathTemplate<T>, private val uScheme: PathTemplate<U>) : PathTemplate<HCons<U, T>> {
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

class PrefixedPathTemplate<T>(private val prefix: PathTemplate<Empty>, private val rest: PathTemplate<T>) : PathTemplate<T> {
    override fun pathElementsFrom(value: T) =
        prefix.pathElementsFrom(Empty) + rest.pathElementsFrom(value)
    
    override fun parsePathElements(pathElements: List<String>) =
        prefix.parsePathElements(pathElements)
            ?.let { (_, restPathElements) -> rest.parsePathElements(restPathElements) }
    
    override fun monitoredPathElementsFrom(value: T) =
        prefix.monitoredPathElementsFrom(Empty) + rest.monitoredPathElementsFrom(value)
}


class SuffixedPathTemplate<T>(private val first: PathTemplate<T>, private val suffix: PathTemplate<Empty>) : PathTemplate<T> {
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

class RestrictedPathTemplate<T>(private val base: PathTemplate<T>, private val p: (T) -> Boolean) : PathTemplate<T> {
    override fun parsePathElements(pathElements: List<String>) =
        base.parsePathElements(pathElements)?.flatMapFirst { if (p(it)) it else null }
    
    override fun pathElementsFrom(value: T) = base.pathElementsFrom(value)
    
    override fun monitoredPathElementsFrom(value: T) = base.monitoredPathElementsFrom(value)
}

fun <T> tStack(): Projection<T, HStack1<T>> = projection(
    fromParts = { t -> Empty + t },
    toParts = { (t) -> t }
)

class ProjectedPathTemplate<T, U>(
    private val base: PathTemplate<T>,
    private val projection: Projection<T, U>
) : PathTemplate<U> {
    override fun parsePathElements(pathElements: List<String>) =
        base.parsePathElements(pathElements).flatMapFirst { projection.fromParts(it) }
    
    override fun pathElementsFrom(value: U) =
        base.pathElementsFrom(projection.toParts(value))
    
    override fun monitoredPathElementsFrom(value: U) =
        base.monitoredPathElementsFrom(projection.toParts(value))
}

private fun <T1, T2, U> Pair<T1, U>?.flatMapFirst(f: (T1) -> T2?): Pair<T2, U>? =
    this?.let { f(first)?.let { it to second } }

