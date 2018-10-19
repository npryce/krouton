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
        assertEncodeDecode(listOf("hits", "operation: cliff clavin"), "/hits/operation%3A%20cliff%20clavin")
        assertEncodeDecode(listOf("hits", "!!!"), "/hits/%21%21%21")
        assertEncodeDecode(listOf("hits", "x+y"), "/hits/x%2By")
    }
    
    @Test
    fun can_decode_a_plus_if_it_receives_one() {
        // Testing this as a special case because we call through to the UrlDecoder after making the test not
        // actually x-www-url-form-encoded format.
        assertThat("decoding", splitPath("/hits/x+y"), equalTo(listOf("hits", "x+y")))
    }
    
    
    private fun assertEncodeDecode(pathElements: List<String>, path: String) {
        assertThat("encoding", joinPath(pathElements, ::encodePathElement), equalTo(path))
        assertThat("decoding", splitPath(path), equalTo(pathElements))
    }
}
