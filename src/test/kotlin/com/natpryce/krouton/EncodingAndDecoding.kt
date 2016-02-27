package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test


class EncodingAndDecoding {
    @Test
    fun encodes_path_elements() {
        val encoded = (string/string).path("ac/dc" to "zz top")

        assertThat(encoded, equalTo("/ac%2Fdc/zz%20top"))
    }
}
