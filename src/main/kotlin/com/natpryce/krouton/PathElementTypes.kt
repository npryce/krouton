package com.natpryce.krouton

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object string : PathElement<String>() {
    override fun parsePathElement(element: String) = element
}

object int : PathElement<Int>() {
    override fun parsePathElement(element: String) =
            parse<Int, NumberFormatException>(element, String::toInt)
}

object long : PathElement<Long>() {
    override fun parsePathElement(element: String) =
            parse<Long, NumberFormatException>(element, String::toLong)
}

object double : PathElement<Double>() {
    override fun parsePathElement(element: String) =
            parse<Double, NumberFormatException>(element, String::toDouble)
}

object isoLocalDate : PathElement<LocalDate>() {
    private val format = DateTimeFormatter.ISO_LOCAL_DATE

    override fun parsePathElement(element: String) =
            parse<LocalDate, DateTimeParseException>(element) { LocalDate.parse(element, format) }

    override fun pathElementFrom(value: LocalDate) =
            value.format(format)
}
