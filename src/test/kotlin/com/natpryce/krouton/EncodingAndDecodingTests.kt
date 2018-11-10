package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.junit.JupiterTests
import com.oneeyedmen.minutest.junit.context


class EncodingAndDecodingTests : JupiterTests {
    override val tests = context<Unit> {
        test("percent encodes and decodes path elements") {
            assertEncodeDecode(listOf("hits", "zz top"), "/hits/zz%20top")
            assertEncodeDecode(listOf("hits", "ac/dc"), "/hits/ac%2Fdc")
            assertEncodeDecode(listOf("hits", "? and the mysterians"), "/hits/%3F%20and%20the%20mysterians")
            assertEncodeDecode(listOf("hits", "operation: cliff clavin"), "/hits/operation%3A%20cliff%20clavin")
            assertEncodeDecode(listOf("hits", "!!!"), "/hits/%21%21%21")
            assertEncodeDecode(listOf("hits", "x+y"), "/hits/x%2By")
        }
        
        test("can decode a plus if it receives one") {
            // Testing this as a special case because we call through to the UrlDecoder after making the text not
            // actually x-www-url-form-encoded format.
            assertThat("decoding", splitPath("/hits/x+y"), equalTo(listOf("hits", "x+y")))
        }
    }
    
    fun assertEncodeDecode(pathElements: List<String>, path: String) {
        assertThat("encoding", joinPath(pathElements, ::encodePathElement), equalTo(path))
        assertThat("decoding", splitPath(path), equalTo(pathElements))
    }
}
