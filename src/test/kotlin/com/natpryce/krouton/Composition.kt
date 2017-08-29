package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test

class Composition {
    @Test
    fun combined_routes() {
        assertThat((int / string).parse("/9/alice"), equalTo(Pair(9, "alice")))
        assertThat(("foo" / string / int).parse("/foo/bob/10"), equalTo(Pair("bob", 10)))
    }

    @Test
    fun reverse_route() {
        assertThat((int / string).path(Pair(10, "ten")), equalTo("/10/ten"))
    }

    @Test
    fun root_acts_as_a_zero_path() {
        assertThat((root / string).parse("/foo"), equalTo("foo"))
        assertThat((string / root).parse("/foo"), equalTo("foo"))
        assertThat((root / string / root).parse("/foo"), equalTo("foo"))
    }
    @Test
    fun stress_test() {
        val crazyScheme: UrlScheme<String> = "first"/root/"mid1"/string/"mid2"/root/"last"
        assertThat(crazyScheme.parse("/first/mid1/foo/mid2/last"), equalTo("foo"))
    }
}