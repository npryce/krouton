package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class RepetitionTests {
    @Test
    fun route_repeating_single_string() {
        assertThat((root + string.repeated()).parse("/x/y/z"), equalTo(listOf("x", "y", "z")))
        assertThat((root + "foo" + string.repeated()).parse("/foo/a/b/c/d"), equalTo(listOf("a", "b", "c", "d")))
    }
    
    @Test
    fun route_repeating_parsed_element() {
        assertThat((root + "bar" + int.repeated()).parse("/bar/1/2/3/4"), equalTo(listOf(1, 2, 3, 4)))
    }
    
    @Test
    fun route_repeating_elements_in_middle_of_path() {
        assertThat((root + "x" + int.repeated() + string).parse("/x/1/2/3/y"), equalTo(Empty + listOf(1, 2, 3) + "y"))
        assertThat((root + "x" + int.repeated() + string).parse("/x/1/2/3/4"), absent()) // because +int consumes the final 4 as an int.
    }
    
    @Test
    fun route_repeating_composite_path_elements() {
        assertThat((string + int).repeated().parse("/a/1/b/2/c/3"),
            equalTo(listOf(Empty + "a" + 1, Empty + "b" + 2, Empty + "c" + 3)))
    }
    
    @Test
    fun route_empty_list() {
        assertThat(string.repeated().parse("/"), equalTo(emptyList()))
    }
    
    @Test
    fun reverse_routing() {
        assertThat(string.repeated().path(listOf("a", "b", "c")), equalTo("/a/b/c"))
        assertThat((string + int).repeated().path(listOf(Empty + "a" + 1, Empty + "b" + 2, Empty + "c" + 3)), equalTo("/a/1/b/2/c/3"))
    }
    
    @Test
    fun reverse_routing_empty_list() {
        assertThat(string.repeated().path(emptyList()), equalTo("/"))
        assertThat((root + "foo" + string.repeated()).path(emptyList()), equalTo("/foo"))
    }
}
