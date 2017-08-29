package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test

class Repetition {
    @Test
    fun route_repeating_single_string() {
        assertThat((+string).parse("/x/y/z"), equalTo(listOf("x", "y", "z")))
        assertThat(("foo" / +string).parse("/foo/a/b/c/d"), equalTo(listOf("a", "b", "c", "d")))
    }

    @Test
    fun route_repeating_parsed_element() {
        assertThat(("bar"/+int).parse("/bar/1/2/3/4"), equalTo(listOf(1,2,3,4)))
    }

    @Test
    fun route_repeating_elements_in_middle_of_path() {
        assertThat(("x"/+int/string).parse("/x/1/2/3/y"), equalTo(Pair(listOf(1,2,3),"y")))
        assertThat(("x"/+int/string).parse("/x/1/2/3/4"), absent()) // because +int consumes the final 4 as an int.
    }

    @Test
    fun route_repeating_composite_path_elements() {
        val route = +(string/int)
        assertThat(route.parse("/a/1/b/2/c/3"), equalTo(listOf("a" to 1, "b" to 2, "c" to 3)))
    }

    @Test
    fun route_empty_list() {
        assertThat((+string).parse("/"), equalTo(emptyList()))
    }

    @Test
    fun reverse_routing() {
        assertThat((+string).path(listOf("a","b","c")), equalTo("/a/b/c"))
        assertThat((+(string/int)).path(listOf("a" to 1,"b" to 2, "c" to 3)), equalTo("/a/1/b/2/c/3"))
    }

    @Test
    fun reverse_routing_empty_list() {
        assertThat((+string).path(emptyList()), equalTo("/"))
        assertThat(("foo"/+string).path(emptyList()), equalTo("/foo"))
    }
}
