package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test
import java.time.LocalDate

class SingleElementRoutes {
    @Test
    fun route_single_element_paths() {
        assertThat(string.parse("/foo"), present(equalTo("foo")))
        assertThat(int.parse("/1"), present(equalTo(1)))
        assertThat(double.parse("/1"), present(equalTo(1.0)))
        assertThat(double.parse("/1.0"), present(equalTo(1.0)))
        assertThat(isoLocalDate.parse("/2016-02-25"), present(equalTo(LocalDate.of(2016, 2, 25))))
    }

    @Test
    fun route_single_element_paths_when_failing() {
        assertThat(string.parse("/"), absent())
        assertThat(string.parse("/foo/bar"), absent())
        assertThat(int.parse("/bob"), absent())
        assertThat(isoLocalDate.parse("/2016-XX-88"), absent())
    }

    @Test
    fun reverse_route_for_single_element() {
        assertThat(string.path("foo"), equalTo("/foo"))
        assertThat(int.path(1), equalTo("/1"))
        assertThat(double.path(1.5), equalTo("/1.5"))
    }

}