package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test

class CombinedRoutes {
    @Test
    fun combined_routes() {
        assertThat((int / string).parse("/9/alice"), present(equalTo(Pair(9, "alice"))))
        assertThat(("foo" / string / int).parse("/foo/bob/10"), present(equalTo(Pair("bob", 10))))
    }

    @Test
    fun reverse_route() {
        assertThat((int / string).path(Pair(10, "ten")), equalTo("/10/ten"))
    }
}