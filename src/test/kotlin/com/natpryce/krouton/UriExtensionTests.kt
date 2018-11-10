package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.Context
import com.oneeyedmen.minutest.junit.JupiterTests
import com.oneeyedmen.minutest.junit.context
import java.net.URI

class UriKroutonExtensionsTests : JupiterTests {
    override val tests: Context<Unit, *> = context<Unit> {
        test("extend uri path") {
            val alice = root + "alice"
            val aliceX = alice + string.named("x")
            
            assertThat(URI("http://example.com").extend(alice), equalTo(URI("http://example.com/alice")))
            assertThat(URI("http://example.com/").extend(alice), equalTo(URI("http://example.com/alice")))
            
            assertThat(URI("http://example.com/bob").extend(alice), equalTo(URI("http://example.com/bob/alice")))
            assertThat(URI("http://example.com/bob/").extend(alice), equalTo(URI("http://example.com/bob/alice")))
            
            assertThat(URI("http://example.com/dave").extend(aliceX, "carol"),
                equalTo(URI("http://example.com/dave/alice/carol")))
            assertThat(URI("http://example.com/dave/").extend(aliceX, "carol"),
                equalTo(URI("http://example.com/dave/alice/carol")))
        }
    }
}
