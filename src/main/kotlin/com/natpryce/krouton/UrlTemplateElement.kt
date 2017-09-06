package com.natpryce.krouton

sealed class UrlTemplateElement
data class Literal(val value: String) : UrlTemplateElement()
data class Variable(val name: String) : UrlTemplateElement()
