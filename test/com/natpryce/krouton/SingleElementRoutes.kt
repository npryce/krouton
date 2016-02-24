package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test

class SingleElementRoutes {
    @Test
    fun route_single_element_paths() {
        assertThat(string.parse("/foo"), present(equalTo("foo")))
        assertThat(int.parse("/1"), present(equalTo(1)))
        assertThat(double.parse("/1"), present(equalTo(1.0)))
        assertThat(double.parse("/1.0"), present(equalTo(1.0)))
    }

    @Test
    fun route_single_element_paths_when_failing() {
        assertThat(string.parse("/"), absent())
        assertThat(string.parse("/foo/bar"), absent())
        assertThat(int.parse("/bob"), absent())
    }

    @Test
    fun reverse_route_for_single_element() {
        assertThat(string.path("foo"), equalTo("/foo"))
        assertThat(int.path(1), equalTo("/1"))
        assertThat(double.path(1.5), equalTo("/1.5"))
    }

}