package com.natpryce.krouton


interface UrlScheme<T> {
    fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>?
    fun pathElementsFrom(value: T): List<String>
}

abstract class PathElement<T> : UrlScheme<T> {
    override fun parsePathElements(pathElements: List<String>): Pair<T, List<String>>? =
            pathElements.firstOrNull()
                    ?.let { parsePathElement(it) }
                    ?.let { it to pathElements.drop(1).toList() }

    override fun pathElementsFrom(value: T): List<String> =
            listOf(pathElementFrom(value))

    abstract fun parsePathElement(element: String): T?

    open fun pathElementFrom(value: T): String = value.toString()
}


inline fun <T, reified X : Exception> parse(s: String, parser: (String) -> T): T? =
        try {
            parser(s)
        } catch (e: Exception) {
            if (e is X) {
                null
            } else {
                throw e
            }
        }

object string : PathElement<String>() {
    override fun parsePathElement(element: String) = element
}

object int : PathElement<Int>() {
    override fun parsePathElement(element: String) =
            parse<Int, NumberFormatException>(element, String::toInt)
}

object double : PathElement<Double>() {
    override fun parsePathElement(element: String) =
            parse<Double, NumberFormatException>(element, String::toDouble)
}

fun <T> UrlScheme<T>.parse(s: String) =
        parsePathElements(splitPath(s))?.let {
            val (result, unused) = it
            if (unused.isEmpty()) result else null
        }

// TODO: apply URL decoding to path elements
private fun splitPath(path: String) = path.split("/").filterNot(String::isEmpty)

// TODO: apply URL encoding to path elements
fun <T> UrlScheme<T>.path(value: T) = "/" + pathElementsFrom(value).joinToString("/")


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

        if (parse == null || parse.second != listOf(suffix)) {
            return null
        } else {
            return Pair(parse.first, emptyList())
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
