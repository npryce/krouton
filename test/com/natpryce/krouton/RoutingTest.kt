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

class PrefixedRoutes {
    @Test
    fun route_prefixed_single_element() {
        assertThat(("foo"/string).parse("/foo/bob"), present(equalTo("bob")))
        assertThat(("bar"/int).parse("/bar/99"), present(equalTo(99)))
    }

    @Test
    fun reverse_route_for_prefixed_single_element() {
        assertThat(("bob"/string).path("xxx"), equalTo("/bob/xxx"))
        assertThat(("foo"/int).path(72), equalTo("/foo/72"))
    }

    @Test
    fun route_prefixed_single_element_when_failing() {
        assertThat(("a"/int).parse("/a"), absent())
        assertThat(("b"/int).parse("/b/not-an-int"), absent())
        assertThat(("c"/int).parse("/c/10/unwanted-suffix"), absent())
        assertThat(("d"/int).parse("/c/10"), absent())
    }
}

class SuffixedRoutes {
    @Test
    fun route_prefixed_single_element() {
        assertThat((string/"foo").parse("/bob/foo"), present(equalTo("bob")))
        assertThat((int/"bar").parse("/99/bar"), present(equalTo(99)))
    }

    @Test
    fun reverse_route_for_prefixed_single_element() {
        assertThat((string/"bob").path("xxx"), equalTo("/xxx/bob"))
        assertThat((int/"foo").path(72), equalTo("/72/foo"))
    }

    @Test
    fun route_prefixed_single_element_when_failing() {
        assertThat((int/"a").parse("/10"), absent())
        assertThat((int/"b").parse("/not-an-int/b"), absent())
        assertThat((int/"c").parse("/unwanted-prefix/10/c"), absent())
        assertThat((int/"d").parse("/10/unwanted-suffix"), absent())
    }
}


class CombinedRoutes {
    @Test
    fun combined_routes() {
        assertThat((int/string).parse("/9/alice"), present(equalTo(Pair(9, "alice"))))
        assertThat(("foo"/string/int).parse("/foo/bob/10"), present(equalTo(Pair("bob", 10))))
    }

    @Test
    fun reverse_route() {
        assertThat((int/string).path(Pair(10, "ten")), equalTo("/10/ten"))
    }
}