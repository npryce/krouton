package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.util.*


//class MappingTests {
//    object MillisSinceEpoch : Projection1<Long, Date> {
//        override fun fromParts(t: Long): Date? = Date(t)
//        override fun toParts(u: Date) = u.time
//    }
//
//    val timestamps = long asA MillisSinceEpoch
//
//    @Test
//    fun route_for_abstracted_scalar() {
//        assertThat(timestamps.parse("/30"), equalTo(Date(30)))
//    }
//
//    @Test
//    fun reverse_routing_for_abstracted_scalar() {
//        assertThat(timestamps.path(Date(1000)), equalTo("/1000"))
//    }
//
//
//    data class Score(val name: String, val score: Int) {
//        companion object : Projection<TStack2<Int,String>, Score> {
//            override fun fromParts(parts: TStack2<Int,String>) = Score(parts.component1(), parts.component2())
//            override fun toParts(mapped: Score): TStack2<Int, String> = Empty + mapped.name + mapped.score
//        }
//    }
//
//    val scores = string + int asA Score
//
//    @Test
//    fun route_for_abstracted_pair() {
//        assertThat(scores.parse("/bob/30"), equalTo(Score("bob", 30)))
//    }
//
//    @Test
//    fun reverse_routing_for_abstracted_pair() {
//        assertThat(scores.path(Score("alice", 100)), equalTo("/alice/100"))
//    }
//
//    data class TimestampedScore(val timestamp: Date, val score: Score) {
//        companion object : Projection2<Date, Score, TimestampedScore> {
//            override fun fromParts(t1: Date, t2: Score) = TimestampedScore(t1, t2)
//
//            override fun toParts(u: TimestampedScore): Pair<Date, Score> {
//                return u.timestamp to u.score
//            }
//        }
//    }
//
//    val timestampedScores = (root + "at" + timestamps + "score" + scores) asA TimestampedScore
//
//    @Test
//    fun route_for_composed_abstractions() {
//        assertThat(timestampedScores.parse("/at/1000/score/alice/20"), equalTo(
//            TimestampedScore(Date(1000), Score("alice", 20))))
//    }
//
//    @Test
//    fun reverse_routing_for_composed_abstractions() {
//        assertThat(timestampedScores.path(TimestampedScore(Date(3000), Score("bob", 1))),
//            equalTo("/at/3000/score/bob/1"))
//    }
//}
