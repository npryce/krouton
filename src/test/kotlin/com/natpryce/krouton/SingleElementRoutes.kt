package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test
import java.time.LocalDate

class SingleElementRoutes {
    @Test
    fun strings() {
        assertThat(string.parse("/foo"), present(equalTo("foo")))

        assertThat(string.parse("/"), absent())
        assertThat(string.parse("/foo/bar"), absent())

        assertThat(string.path("foo"), equalTo("/foo"))
    }

    @Test
    fun ints() {
        assertThat(int.parse("/1"), present(equalTo(1)))
        assertThat(int.parse("/-4"), present(equalTo(-4)))
        assertThat(int.parse("/010"), present(equalTo(10)))

        assertThat(int.parse("/bob"), absent())

        assertThat(int.path(1), equalTo("/1"))
        assertThat(int.path(-1), equalTo("/-1"))
    }

    @Test
    fun doubles() {
        assertThat(double.parse("/1"), present(equalTo(1.0)))
        assertThat(double.parse("/1.0"), present(equalTo(1.0)))
        assertThat(double.parse("/-2.0"), present(equalTo(-2.0)))

        assertThat(double.parse("/bob"), absent())

        assertThat(double.path(1.5), equalTo("/1.5"))
        assertThat(double.path(-3.25), equalTo("/-3.25"))
    }

    @Test
    fun iso_dates() {
        assertThat(isoLocalDate.parse("/2016-02-25"), present(equalTo(LocalDate.of(2016, 2, 25))))
        assertThat(isoLocalDate.parse("/2016-XX-88"), absent())
    }

    enum class Axis {
        X, Y, Z
    }

    @Test
    fun enums() {
        val axis = enum<Axis>()

        assertThat(axis.parse("/X"), present(equalTo(Axis.X)))
        assertThat(axis.parse("/Y"), present(equalTo(Axis.Y)))
        assertThat(axis.parse("/Z"), present(equalTo(Axis.Z)))

        assertThat(axis.parse("/x"), absent())
        assertThat(axis.parse("/a"), absent())
        assertThat(axis.parse("/ddd"), absent())

        assertThat(axis.path(Axis.Z), equalTo("/Z"))
    }
}