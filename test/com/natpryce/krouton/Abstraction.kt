package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test
import java.util.*


class Abstraction {
    object MillisSinceEpoch : Abstraction1<Long,Date> {
        override fun fromPath(t: Long): Date? = Date(t)
        override fun toPath(u: Date) = u.time
    }

    val dateRoute = long asA MillisSinceEpoch

    @Test
    fun route_for_abstracted_scalar() {
        assertThat(dateRoute.parse("/30"), present(equalTo(Date(30))))
    }

    @Test
    fun reverse_routing_for_abstracted_scalar() {
        assertThat(dateRoute.path(Date(1000)), equalTo("/1000"))
    }


    data class Score(val name: String, val score: Int) {
        companion object : Abstraction2<String,Int,Score> {
            override fun fromPath(t1: String, t2: Int): Score? = Score(t1, t2)
            override fun toPath(u: Score): Pair<String, Int> = u.name to u.score
        }
    }

    val scoreRoute = (string / int) asA Score

    @Test
    fun route_for_abstracted_pair() {
        assertThat(scoreRoute.parse("/bob/30"), present(equalTo(Score("bob", 30))))
    }

    @Test
    fun reverse_routing_for_abstracted_pair() {
        assertThat(scoreRoute.path(Score("alice", 100)), equalTo("/alice/100"))
    }
}