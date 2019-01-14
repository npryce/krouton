package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.experimental.randomTest
import com.oneeyedmen.minutest.rootContext


fun `encoding and decoding`() = rootContext<Unit> {
    val examples = listOf(
        listOf("hits", "zz top") to "/hits/zz%20top",
        listOf("hits", "ac/dc") to "/hits/ac%2Fdc",
        listOf("hits", "? and the mysterians") to "/hits/%3F%20and%20the%20mysterians",
        listOf("hits", "operation: cliff clavin") to "/hits/operation%3A%20cliff%20clavin",
        listOf("hits", "!!!") to "/hits/%21%21%21",
        listOf("hits", "x+y") to "/hits/x%2By"
    )
    
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
    
    randomTest("fuzzing") { random ->
        repeat(100) { n ->
            val original = random.nextBytes(16).toString(Charsets.UTF_8)
            
            val encoded = encodePathElement(original)
            val decoded = decodePathElement(encoded)
            
            assertThat(decoded, equalTo(original))
        }
    }
}
