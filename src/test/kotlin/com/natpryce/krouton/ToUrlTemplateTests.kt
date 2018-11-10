package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.ProjectionTests.Score
import com.oneeyedmen.minutest.junit.JupiterTests
import com.oneeyedmen.minutest.junit.context

class ToUrlTemplateTests : JupiterTests {
    override val tests = context<Unit> {
        test("url templates for the loosely typed") {
            val nameAndScorePath = +"player" + string.named("p").monitored() + "score" + int.named("s")
            val scorePath = nameAndScorePath asA Score
        
            assertThat(nameAndScorePath.toUrlTemplate(), equalTo("/player/{p}/score/{s}"))
            assertThat(scorePath.toUrlTemplate(), equalTo("/player/{p}/score/{s}"))
        }
    }
}
