package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.minutest.experimental.randomTest
import dev.minutest.junit.JUnit5Minutests
import dev.minutest.rootContext
import org.junit.platform.commons.annotation.Testable

@Testable
fun `encoding and decoding`() = rootContext {
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
    
    randomTest("fuzzing") { random, _ ->
        repeat(100) { _ ->
            val original = random.nextBytes(16).toString(Charsets.UTF_8)
            
            val encoded = encodePathElement(original)
            val decoded = decodePathElement(encoded)
            
            assertThat(decoded, equalTo(original))
        }
    }
}
