package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.nio.file.Path
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KProperty

class SingleElementRouteTests {
    private val s by string
    private val i by int
    private val d by double
    private val start by isoLocalDate
    private val loc by locale
    
    
    @Test
    fun strings() {
        assertThat(s.parse("/foo"), equalTo("foo"))

        assertThat(s.parse("/"), absent())
        assertThat(s.parse("/foo/bar"), absent())

        assertThat(s.path("foo"), equalTo("/foo"))
    }
    
    @Test
    fun ints() {
        assertThat(i.parse("/1"), equalTo(1))
        assertThat(i.parse("/-4"), equalTo(-4))
        assertThat(i.parse("/010"), equalTo(10))

        assertThat(i.parse("/bob"), absent())

        assertThat(i.path(1), equalTo("/1"))
        assertThat(i.path(-1), equalTo("/-1"))
    }
    
    @Test
    fun doubles() {
        assertThat(d.parse("/1"), equalTo(1.0))
        assertThat(d.parse("/1.0"), equalTo(1.0))
        assertThat(d.parse("/-2.0"), equalTo(-2.0))

        assertThat(d.parse("/bob"), absent())

        assertThat(d.path(1.5), equalTo("/1.5"))
        assertThat(d.path(-3.25), equalTo("/-3.25"))
    }
    
    
    @Test
    fun iso_dates() {
        assertThat(start.parse("/2016-02-25"), equalTo(LocalDate.of(2016, 2, 25)))
        assertThat(start.parse("/2016-XX-88"), absent())
    }

    enum class Axis {
        X, Y, Z
    }
    
    @Test
    fun enums() {
        val axis : VariablePathElement<Axis> by enum<Axis>()
        
        assertThat(axis.parse("/X"), equalTo(Axis.X))
        assertThat(axis.parse("/Y"), equalTo(Axis.Y))
        assertThat(axis.parse("/Z"), equalTo(Axis.Z))

        assertThat(axis.parse("/x"), absent())
        assertThat(axis.parse("/a"), absent())
        assertThat(axis.parse("/ddd"), absent())

        assertThat(axis.path(Axis.Z), equalTo("/Z"))
    }

    @Test
    fun locales() {
        assertThat(loc.parse("/fr-FR"), equalTo(Locale.FRANCE))
        assertThat(loc.path(Locale.FRANCE), equalTo("/fr-FR"))
    }
}