package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test

class Suffixion {
    @Test
    fun route_suffixed_single_element() {
        assertThat((string / "foo").parse("/bob/foo"), equalTo("bob"))
        assertThat((int / "bar").parse("/99/bar"), equalTo(99))
    }

    @Test
    fun reverse_route_for_suffixed_single_element() {
        assertThat((string / "bob").path("xxx"), equalTo("/xxx/bob"))
        assertThat((int / "foo").path(72), equalTo("/72/foo"))
    }

    @Test
    fun route_sufffixed_single_element_when_failing() {
        assertThat((int / "a").parse("/10"), absent())
        assertThat((int / "b").parse("/not-an-int/b"), absent())
        assertThat((int / "c").parse("/unwanted-prefix/10/c"), absent())
        assertThat((int / "d").parse("/10/unwanted-suffix"), absent())
    }
}