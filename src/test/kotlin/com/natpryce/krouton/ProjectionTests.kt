package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.util.Date


class ProjectionTests {
    object MillisSinceEpoch : Projection<Long, Date> {
        override fun fromParts(parts: Long): Date? = Date(parts)
        override fun toParts(mapped: Date) = mapped.time
    }

    private val timestamp = long.named("timestamp") asA MillisSinceEpoch

    @Test
    fun route_for_abstracted_scalar() {
        assertThat(timestamp.parse("/30"), equalTo(Date(30)))
    }

    @Test
    fun reverse_routing_for_abstracted_scalar() {
        assertThat(timestamp.path(Date(1000)), equalTo("/1000"))
    }

    
    data class Score(val name: String, val score: Int) {
        companion object : Projection<Tuple2<String,Int>, Score> {
            override fun fromParts(parts: Tuple2<String, Int>) = Score(parts.val1, parts.val2)
            override fun toParts(mapped: Score) = tuple(mapped.name, mapped.score)
        }
    }

    private val scores = string + int asA Score

    @Test
    fun route_for_abstracted_pair() {
        assertThat(scores.parse("/bob/30"), equalTo(Score("bob", 30)))
    }

    @Test
    fun reverse_routing_for_abstracted_pair() {
        assertThat(scores.path(Score("alice", 100)), equalTo("/alice/100"))
    }
    
    data class TimestampedScore(val timestamp: Date, val score: Score) {
        companion object : Projection<Tuple2<Date, Score>, TimestampedScore> {
            override fun fromParts(parts: Tuple2<Date,Score>) = TimestampedScore(parts.val1, parts.val2)
            override fun toParts(mapped: TimestampedScore) = tuple(mapped.timestamp, mapped.score)
        }
    }
    
    private val timestampedScores = (root + "at" + timestamp + "score" + scores) asA TimestampedScore

    @Test
    fun route_for_composed_projections() {
        assertThat(timestampedScores.parse("/at/1000/score/alice/20"), equalTo(
            TimestampedScore(Date(1000), Score("alice", 20))))
    }

    @Test
    fun reverse_routing_for_composed_projections() {
        assertThat(timestampedScores.path(TimestampedScore(Date(3000), Score("bob", 1))),
            equalTo("/at/3000/score/bob/1"))
    }
}
