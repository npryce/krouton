@file:JvmName("PathElementTypes")

package com.natpryce.krouton

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

val string = object : PathElementType<String> {
    override fun parsePathElement(element: String) = element
}

val int = object : PathElementType<Int> {
    override fun parsePathElement(element: String) =
        parse<Int, NumberFormatException>(element, String::toInt)
}

val long = object : PathElementType<Long> {
    override fun parsePathElement(element: String) =
        parse<Long, NumberFormatException>(element, String::toLong)
}

val double = object : PathElementType<Double> {
    override fun parsePathElement(element: String) =
        parse<Double, NumberFormatException>(element, String::toDouble)
}

inline fun <reified E : Enum<E>> enum(): PathElementType<E> = object : PathElementType<E> {
    override fun parsePathElement(element: String)
        = try {
        java.lang.Enum.valueOf(E::class.java, element)
    }
    catch (e: IllegalArgumentException) {
        null
    }
}

val isoLocalDate = object : PathElementType<LocalDate> {
    private val format = DateTimeFormatter.ISO_LOCAL_DATE
    
    override fun parsePathElement(element: String) =
        parse<LocalDate, DateTimeParseException>(element) { LocalDate.parse(element, format) }
    
    override fun pathElementFrom(value: LocalDate) =
        value.format(format)
}

val locale = object : PathElementType<Locale> {
    override fun parsePathElement(element: String) = Locale.forLanguageTag(element)
    override fun pathElementFrom(value: Locale) = value.toLanguageTag()
}

