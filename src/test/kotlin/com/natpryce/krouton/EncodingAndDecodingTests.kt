package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.junit.JupiterTests
import com.oneeyedmen.minutest.junit.context


class EncodingAndDecodingTests : JupiterTests {
    val examples = listOf(
        listOf("hits", "zz top") to "/hits/zz%20top",
        listOf("hits", "ac/dc") to "/hits/ac%2Fdc",
        listOf("hits", "? and the mysterians") to "/hits/%3F%20and%20the%20mysterians",
        listOf("hits", "operation: cliff clavin") to "/hits/operation%3A%20cliff%20clavin",
        listOf("hits", "!!!") to "/hits/%21%21%21",
        listOf("hits", "x+y") to "/hits/x%2By"
    )
    
    override val tests = context<Unit> {
        examples.forEach { (elements, encoded) ->
            test("$elements <-> $encoded") {
                assertThat("encoding", joinPath(elements, ::encodePathElement), equalTo(encoded))
                assertThat("decoding", splitPath(encoded), equalTo(elements))
            }
        }
        
        test("can decode a plus if it receives one") {
            // Testing this as a special case because we call through to the UrlDecoder after making the text not
            // actually x-www-url-form-encoded format.
            assertThat("decoding", splitPath("/hits/x+y"), equalTo(listOf("hits", "x+y")))
        }
    }
}
