package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class CompositionTests {
    val s = string.named("s")
    val i = int.named("i")
    
    @Test
    fun route_prefixed_single_element() {
        assertThat((root + "foo" + s).parse("/foo/bob"), equalTo("bob"))
        assertThat((root + "bar" + i).parse("/bar/99"), equalTo(99))
    }
    
    @Test
    fun reverse_route_for_prefixed_single_element() {
        assertThat((root + "bob" + s).path("xxx"), equalTo("/bob/xxx"))
        assertThat((root + "foo" + i).path(72), equalTo("/foo/72"))
    }
    
    @Test
    fun route_prefixed_single_element_when_failing() {
        assertThat((root + "a" + i).parse("/a"), absent())
        assertThat((root + "b" + i).parse("/b/not-an-int"), absent())
        assertThat((root + "c" + i).parse("/c/10/unwanted-suffix"), absent())
        assertThat((root + "d" + i).parse("/c/10"), absent())
    }
    
    @Test
    fun route_suffixed_single_element() {
        assertThat((s + "foo").parse("/bob/foo"), equalTo("bob"))
        assertThat((i + "bar").parse("/99/bar"), equalTo(99))
    }
    
    @Test
    fun reverse_route_for_suffixed_single_element() {
        assertThat((s + "bob").path("xxx"), equalTo("/xxx/bob"))
        assertThat((i + "foo").path(72), equalTo("/72/foo"))
    }
    
    @Test
    fun route_suffixed_single_element_when_failing() {
        assertThat((i + "a").parse("/10"), absent())
        assertThat((i + "b").parse("/not-an-int/b"), absent())
        assertThat((i + "c").parse("/unwanted-prefix/10/c"), absent())
        assertThat((i + "d").parse("/10/unwanted-suffix"), absent())
    }
    
    @Test
    fun combined_routes() {
        assertThat((i + s).parse("/9/alice"), equalTo(Empty + 9 + "alice"))
        assertThat((root + i + s).parse("/9/alice"), equalTo(Empty + 9 + "alice"))
        assertThat((root + "foo" + s + i).parse("/foo/bob/10"), equalTo(Empty + "bob" + 10))
    }
    
    @Test
    fun reverse_route() {
        assertThat((i + s).path(Empty + 10 + "ten"), equalTo("/10/ten"))
        assertThat((i + s).path(10, "ten"), equalTo("/10/ten"))
    }
    
    @Test
    fun root_acts_as_a_zero_path() {
        assertThat((root + s).parse("/foo"), equalTo("foo"))
        assertThat((s + root).parse("/foo"), equalTo("foo"))
        assertThat((root + s + root).parse("/foo"), equalTo("foo"))
    }
    
    @Test
    fun stress_test() {
        val crazyScheme: UrlScheme<String> = root + "first" + "mid1" + s + "mid2" + "last"
        assertThat(crazyScheme.parse("/first/mid1/foo/mid2/last"), equalTo("foo"))
    }
}
