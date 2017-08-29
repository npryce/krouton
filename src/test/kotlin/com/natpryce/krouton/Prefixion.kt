package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test

class Prefixion {
    @Test
    fun route_prefixed_single_element() {
        assertThat(("foo" / string).parse("/foo/bob"), equalTo("bob"))
        assertThat(("bar" / int).parse("/bar/99"), equalTo(99))
    }

    @Test
    fun reverse_route_for_prefixed_single_element() {
        assertThat(("bob" / string).path("xxx"), equalTo("/bob/xxx"))
        assertThat(("foo" / int).path(72), equalTo("/foo/72"))
    }

    @Test
    fun route_prefixed_single_element_when_failing() {
        assertThat(("a" / int).parse("/a"), absent())
        assertThat(("b" / int).parse("/b/not-an-int"), absent())
        assertThat(("c" / int).parse("/c/10/unwanted-suffix"), absent())
        assertThat(("d" / int).parse("/c/10"), absent())
    }
}