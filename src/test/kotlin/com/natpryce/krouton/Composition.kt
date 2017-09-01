package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class Composition {
    @Test
    fun route_prefixed_single_element() {
        assertThat((root + "foo" + string).parse("/foo/bob"), equalTo("bob"))
        assertThat((root + "bar" + int).parse("/bar/99"), equalTo(99))
    }
    
    @Test
    fun reverse_route_for_prefixed_single_element() {
        assertThat((root + "bob" + string).path("xxx"), equalTo("/bob/xxx"))
        assertThat((root + "foo" + int).path(72), equalTo("/foo/72"))
    }
    
    @Test
    fun route_prefixed_single_element_when_failing() {
        assertThat((root + "a" + int).parse("/a"), absent())
        assertThat((root + "b" + int).parse("/b/not-an-int"), absent())
        assertThat((root + "c" + int).parse("/c/10/unwanted-suffix"), absent())
        assertThat((root + "d" + int).parse("/c/10"), absent())
    }
    
    @Test
    fun combined_routes() {
        assertThat((int + string).parse("/9/alice"), equalTo(Empty + 9 + "alice"))
        assertThat((root + int + string).parse("/9/alice"), equalTo(Empty + 9 + "alice"))
        assertThat((root + "foo" + string + int).parse("/foo/bob/10"), equalTo(Empty + "bob" + 10))
    }
    
    @Test
    fun reverse_route() {
        assertThat((int + string).path(Empty + 10 + "ten"), equalTo("/10/ten"))
    }
    
    @Test
    fun root_acts_as_a_zero_path() {
        assertThat((root + string).parse("/foo"), equalTo("foo"))
        assertThat((string + root).parse("/foo"), equalTo("foo"))
        assertThat((root + string + root).parse("/foo"), equalTo("foo"))
    }
    
    @Test
    fun stress_test() {
        val crazyScheme: UrlScheme<String> = root + "first" + "mid1" + string + "mid2" + "last"
        assertThat(crazyScheme.parse("/first/mid1/foo/mid2/last"), equalTo("foo"))
    }
}
