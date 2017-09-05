package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class CompositionTests {
    @Test
    fun route_prefixed_single_element() {
        assertThat((+"foo" + string).parse("/foo/bob"), equalTo("bob"))
        assertThat((+"bar" + int).parse("/bar/99"), equalTo(99))
    }
    
    @Test
    fun reverse_route_for_prefixed_single_element() {
        assertThat((+"bob" + string).path("xxx"), equalTo("/bob/xxx"))
        assertThat((+"foo" + int).path(72), equalTo("/foo/72"))
    }
    
    @Test
    fun route_prefixed_single_element_when_failing() {
        assertThat((+"a" + int).parse("/a"), absent())
        assertThat((+"b" + int).parse("/b/not-an-int"), absent())
        assertThat((+"c" + int).parse("/c/10/unwanted-suffix"), absent())
        assertThat((+"d" + int).parse("/c/10"), absent())
    }
    
    @Test
    fun route_suffixed_single_element() {
        assertThat((string + "foo").parse("/bob/foo"), equalTo("bob"))
        assertThat((int + "bar").parse("/99/bar"), equalTo(99))
    }
    
    @Test
    fun reverse_route_for_suffixed_single_element() {
        assertThat((string + "bob").path("xxx"), equalTo("/xxx/bob"))
        assertThat((int + "foo").path(72), equalTo("/72/foo"))
    }
    
    @Test
    fun route_suffixed_single_element_when_failing() {
        assertThat((int + "a").parse("/10"), absent())
        assertThat((int + "b").parse("/not-an-int/b"), absent())
        assertThat((int + "c").parse("/unwanted-prefix/10/c"), absent())
        assertThat((int + "d").parse("/10/unwanted-suffix"), absent())
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
        assertThat((int + string).path(10, "ten"), equalTo("/10/ten"))
    }
    
    @Test
    fun root_acts_as_a_zero_path() {
        assertThat((root + string).parse("/foo"), equalTo("foo"))
        assertThat((string + root).parse("/foo"), equalTo("foo"))
        assertThat((root + string + root).parse("/foo"), equalTo("foo"))
    }
    
    @Test
    fun unary_plus_is_syntactic_sugar_for_prefixed_by_root() {
        assertThat((root + string).path("foo"), equalTo((+string).path("foo")))
        assertThat((+ "blah").path(), equalTo((root + "blah").path()))
    }
    
    @Test
    fun stress_test() {
        val crazyScheme: PathTemplate<String> = root + "first" + "mid1" + string + "mid2" + "last"
        assertThat(crazyScheme.parse("/first/mid1/foo/mid2/last"), equalTo("foo"))
    }
}
