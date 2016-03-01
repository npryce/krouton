package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test

class Composition {
    @Test
    fun combined_routes() {
        assertThat((int / string).parse("/9/alice"), present(equalTo(Pair(9, "alice"))))
        assertThat(("foo" / string / int).parse("/foo/bob/10"), present(equalTo(Pair("bob", 10))))
    }

    @Test
    fun reverse_route() {
        assertThat((int / string).path(Pair(10, "ten")), equalTo("/10/ten"))
    }

    @Test
    fun root_acts_as_a_zero_path() {
        assertThat((root / string).parse("/foo"), present(equalTo("foo")))
        assertThat((string / root).parse("/foo"), present(equalTo("foo")))
        assertThat((root / string / root).parse("/foo"), present(equalTo("foo")))
    }
    @Test
    fun stress_test() {
        val crazyScheme: UrlScheme<String> = "first"/root/"mid1"/string/"mid2"/root/"last"
        assertThat(crazyScheme.parse("/first/mid1/foo/mid2/last"), present(equalTo("foo")))
    }
}