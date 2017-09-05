package com.natpryce.krouton

sealed class MonitoredPathElement
data class Literal(val value: String) : MonitoredPathElement()
data class Variable(val name: String) : MonitoredPathElement()
