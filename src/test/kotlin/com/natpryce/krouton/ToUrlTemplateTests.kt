package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.experimental.context


val `converting krouton routes to url template syntax` = context<Unit> {
    val nameAndScorePath = +"player" + string.named("p").monitored() + "score" + int.named("s")
    val scorePath = nameAndScorePath asA Score
    
    assertThat(nameAndScorePath.toUrlTemplate(), equalTo("/player/{p}/score/{s}"))
    assertThat(scorePath.toUrlTemplate(), equalTo("/player/{p}/score/{s}"))
}
