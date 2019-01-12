package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.rootContext
import java.net.URI

fun `extend URI path with template and parameters`() = rootContext<Unit> {
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
