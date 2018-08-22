package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.net.URI

class UriKroutonExtensionsTests {
    @Test
    fun extend_uri_path() {
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
