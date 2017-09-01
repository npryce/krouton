package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test


class EncodingAndDecodingTests {
    @Test
    fun percent_encodes_and_decodes_path_elements() {
        assertEncodeDecode(listOf("hits", "zz top"), "/hits/zz%20top")
        assertEncodeDecode(listOf("hits", "ac/dc"), "/hits/ac%2Fdc")
        assertEncodeDecode(listOf("hits", "? and the mysterians"), "/hits/%3F%20and%20the%20mysterians")
    }

    fun assertEncodeDecode(pathElements: List<String>, path: String) {
        assertThat("encoding", joinPath(pathElements), equalTo(path))
        assertThat("decoding", splitPath(path), equalTo(pathElements))
    }
}
