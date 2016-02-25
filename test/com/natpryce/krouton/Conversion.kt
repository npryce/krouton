package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.Test
import java.util.*


class Conversion {
    object MillisSinceEpoch : Has1Part<Long,Date> {
        override fun fromParts(t: Long): Date? = Date(t)
        override fun toParts(u: Date) = u.time
    }

    val timestamps = long asA MillisSinceEpoch

    @Test
    fun route_for_abstracted_scalar() {
        assertThat(timestamps.parse("/30"), present(equalTo(Date(30))))
    }

    @Test
    fun reverse_routing_for_abstracted_scalar() {
        assertThat(timestamps.path(Date(1000)), equalTo("/1000"))
    }


    data class Score(val name: String, val score: Int) {
        companion object : Has2Parts<String,Int,Score> {
            override fun fromParts(t1: String, t2: Int): Score? = Score(t1, t2)
            override fun toParts(u: Score): Pair<String, Int> = u.name to u.score
        }
    }

    val scores = string/int asA Score

    @Test
    fun route_for_abstracted_pair() {
        assertThat(scores.parse("/bob/30"), present(equalTo(Score("bob", 30))))
    }

    @Test
    fun reverse_routing_for_abstracted_pair() {
        assertThat(scores.path(Score("alice", 100)), equalTo("/alice/100"))
    }

    data class TimestampedScore(val timestamp: Date, val score: Score) {
        companion object : Has2Parts<Date,Score,TimestampedScore> {
            override fun fromParts(t1: Date, t2: Score) = TimestampedScore(t1, t2)

            override fun toParts(u: TimestampedScore): Pair<Date, Score> {
                return u.timestamp to u.score
            }
        }
    }

    val timestampedScores = "at"/timestamps/"score"/scores asA TimestampedScore

    @Test
    fun route_for_composed_abstractions() {
        assertThat(timestampedScores.parse("/at/1000/score/alice/20"), present(equalTo(
                TimestampedScore(Date(1000), Score("alice", 20)))))
    }

    @Test
    fun reverse_routing_for_composed_abstractions() {
        assertThat(timestampedScores.path(TimestampedScore(Date(3000), Score("bob", 1))),
                equalTo("/at/3000/score/bob/1"))
    }
}
