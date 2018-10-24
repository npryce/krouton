package com.natpryce.krouton

import kotlin.reflect.KProperty


interface PathTemplate<T> {
    fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>?
    fun pathElementsFrom(value: T): List<String>
    fun templateElements(): List<UrlTemplateElement>
    fun monitoredTemplateElementsFrom(value: T): List<UrlTemplateElement>
}

typealias PathTemplate0 = PathTemplate<Unit>
typealias PathTemplate1<A> = PathTemplate<A>
typealias PathTemplate2<A, B> = PathTemplate<Tuple2<A, B>>
typealias PathTemplate3<A, B, C> = PathTemplate<Tuple3<A, B, C>>
typealias PathTemplate4<A, B, C, D> = PathTemplate<Tuple4<A, B, C, D>>
typealias PathTemplate5<A, B, C, D, E> = PathTemplate<Tuple5<A, B, C, D, E>>


fun <T> PathTemplate<T>.parse(s: String): T? {
    return parse(splitPath(s))
}

fun <T> PathTemplate<T>.parse(splitPath: List<String>): T? =
    parsePathElements(splitPath)?.let { (result, unused) -> if (unused.isEmpty()) result else null }

fun <T> PathTemplate<T>.path(value: T): String =
    joinPath(pathElementsFrom(value), ::encodePathElement)

fun <T> PathTemplate<T>.monitoredPath(value: T): String =
    joinPath(monitoredTemplateElementsFrom(value), ::encodeUrlTemplatePathElement)

fun <T> PathTemplate<T>.toUrlTemplate(): String =
    joinPath(templateElements(), ::encodeUrlTemplatePathElement)


internal fun splitPath(path: String) =
    path.split("/").filterNot(String::isEmpty).map(::decodePathElement)

internal fun <T> joinPath(pathElements: List<T>, transform: (T) -> CharSequence) =
    "/" + pathElements.joinToString("/", transform = transform)

internal fun encodeUrlTemplatePathElement(it: UrlTemplateElement): CharSequence {
    return when (it) {
        is Variable -> "{" + it.name + "}"
        is Literal -> encodePathElement(it.value)
    }
}

/**
 * The root of path, <code>/</code>.
 */
object root : PathTemplate<Unit> {
    override fun parsePathElements(pathElements: List<String>) = Pair(Unit, pathElements)
    override fun pathElementsFrom(value: Unit) = emptyList<String>()
    override fun templateElements() = emptyList<UrlTemplateElement>()
    override fun monitoredTemplateElementsFrom(value: Unit) = emptyList<UrlTemplateElement>()
}

sealed class PathElement<T> : PathTemplate<T> {
    final override fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>? =
        pathElements.firstOrNull()
            ?.let { parsePathElement(it) }
            ?.let { it to pathElements.drop(1) }
    
    abstract fun parsePathElement(element: String): T?
    
    final override fun pathElementsFrom(value: T) = listOf(pathElementFrom(value))
    abstract fun pathElementFrom(value: T): String
    
    final override fun templateElements() = listOf(templateElement())
    abstract fun templateElement(): UrlTemplateElement
    
    final override fun monitoredTemplateElementsFrom(value: T) = listOf(monitoredPathElementFrom(value))
    abstract fun monitoredPathElementFrom(value: T): UrlTemplateElement
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
    
    override fun templateElement() =
        Variable(name)
    
    override fun monitoredPathElementFrom(value: T): UrlTemplateElement =
        if (isMonitored) Literal(pathElementFrom(value)) else Variable(name)
    
    fun named(name: String) = VariablePathElement(type = type, name = name, isMonitored = isMonitored)
    fun monitored() = VariablePathElement(type = type, name = name, isMonitored = true)
}


operator fun <T> PathElementType<T>.getValue(obj: Any?, property: KProperty<*>): VariablePathElement<T> =
    VariablePathElement(this, property.name)

operator fun <T> VariablePathElement<T>.getValue(obj: Any?, property: KProperty<*>): VariablePathElement<T> =
    this.named(property.name)


class LiteralPathElement(private val pathElement: String) : PathElement<Unit>() {
    override fun parsePathElement(element: String) = Unit.takeIf { element == pathElement }
    override fun pathElementFrom(value: Unit) = pathElement
    override fun templateElement() = Literal(pathElement)
    override fun monitoredPathElementFrom(value: Unit) = Literal(pathElement)
}

class AppendedPathTemplate<T, U>(private val tScheme: PathTemplate<T>, private val uScheme: PathTemplate<U>) : PathTemplate<Tuple2<T, U>> {
    override fun parsePathElements(pathElements: List<String>) =
        tScheme.parsePathElements(pathElements)?.let { (t, uPathElements) ->
            uScheme.parsePathElements(uPathElements)?.let { (u, rest) -> tuple(t, u) to rest }
        }
    
    override fun pathElementsFrom(value: Tuple2<T,U>) =
        tScheme.pathElementsFrom(value.val1) + uScheme.pathElementsFrom(value.val2)
    
    override fun templateElements() =
        tScheme.templateElements() + uScheme.templateElements()
    
    override fun monitoredTemplateElementsFrom(value: Tuple2<T,U>) =
        tScheme.monitoredTemplateElementsFrom(value.val1) + uScheme.monitoredTemplateElementsFrom(value.val2)
}

class PrefixedPathTemplate<T>(private val prefix: PathTemplate<Unit>, private val rest: PathTemplate<T>) : PathTemplate<T> {
    override fun pathElementsFrom(value: T) =
        prefix.pathElementsFrom(Unit) + rest.pathElementsFrom(value)
    
    override fun parsePathElements(pathElements: List<String>) =
        prefix.parsePathElements(pathElements)
            ?.let { (_, restPathElements) -> rest.parsePathElements(restPathElements) }
    
    override fun templateElements() =
        prefix.templateElements() + rest.templateElements()
    
    override fun monitoredTemplateElementsFrom(value: T) =
        prefix.monitoredTemplateElementsFrom(Unit) + rest.monitoredTemplateElementsFrom(value)
}


class SuffixedPathTemplate<T>(private val first: PathTemplate<T>, private val suffix: PathTemplate<Unit>) : PathTemplate<T> {
    override fun pathElementsFrom(value: T) =
        first.pathElementsFrom(value) + suffix.pathElementsFrom(Unit)
    
    override fun parsePathElements(pathElements: List<String>) =
        first.parsePathElements(pathElements)
            ?.let { (value, restPathElements) ->
                suffix.parsePathElements(restPathElements)
                    ?.let { (_, remainderPathElements) -> Pair(value, remainderPathElements) }
            }
    
    override fun templateElements() =
        first.templateElements() + suffix.templateElements()
    
    override fun monitoredTemplateElementsFrom(value: T) =
        first.monitoredTemplateElementsFrom(value) + suffix.monitoredTemplateElementsFrom(Unit)
}

class RestrictedPathTemplate<T>(private val base: PathTemplate<T>, private val p: (T) -> Boolean) : PathTemplate<T> {
    override fun parsePathElements(pathElements: List<String>) =
        base.parsePathElements(pathElements)?.flatMapFirst { if (p(it)) it else null }
    
    override fun pathElementsFrom(value: T) = base.pathElementsFrom(value)
    override fun templateElements() = base.templateElements()
    override fun monitoredTemplateElementsFrom(value: T) = base.monitoredTemplateElementsFrom(value)
}

class ProjectedPathTemplate<T, U>(
    private val base: PathTemplate<T>,
    private val projection: Projection<T, U>
) : PathTemplate<U> {
    override fun parsePathElements(pathElements: List<String>) =
        base.parsePathElements(pathElements).flatMapFirst { projection.fromParts(it) }
    
    override fun pathElementsFrom(value: U) =
        base.pathElementsFrom(projection.toParts(value))
    
    override fun templateElements() =
        base.templateElements()
    
    override fun monitoredTemplateElementsFrom(value: U) =
        base.monitoredTemplateElementsFrom(projection.toParts(value))
}

private fun <T1, T2, U> Pair<T1, U>?.flatMapFirst(f: (T1) -> T2?): Pair<T2, U>? =
    this?.run { f(first)?.to(second) }
