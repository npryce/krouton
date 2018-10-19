package com.natpryce.krouton

import java.net.URLDecoder
import java.net.URLEncoder

internal fun encodePathElement(original: String) =
    URLEncoder.encode(original, "UTF-8").replace("+", "%20")

internal fun decodePathElement(original: String): String =
    URLDecoder.decode(original.replace("+", "%2B"), "UTF-8")
