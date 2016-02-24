package com.natpryce.krouton

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

