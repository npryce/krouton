package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.ProjectionTests.Score
import org.junit.Test

class ToUrlTemplateTests {
    @Test
    fun `url templates for the loosely typed`() {
        val nameAndScorePath = +"player" + string.named("p").monitored() + "score" + int.named("s")
        val scorePath = nameAndScorePath asA Score
        
        assertThat(nameAndScorePath.toUrlTemplate(), equalTo("/player/{p}/score/{s}"))
        assertThat(scorePath.toUrlTemplate(), equalTo("/player/{p}/score/{s}"))
    }
    
}
