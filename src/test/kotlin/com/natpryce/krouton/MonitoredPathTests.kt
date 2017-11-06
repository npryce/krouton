package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class MonitoredPathTests {
    @Test
    fun `reports symbolic name of variable path element`() {
        assertThat(string.named("s").monitoredPath("foo"), equalTo("/{s}"))
        assertThat(int.named("i").monitoredPath(10), equalTo("/{i}"))
    }
    
    @Test
    fun `reports monitored values`() {
        assertThat(string.named("s").monitored().monitoredPath("foo"), equalTo("/foo"))
        assertThat(int.named("s").monitored().monitoredPath(10), equalTo("/10"))
    }
    
    @Test
    fun `monitored path reporting with composition`() {
        assertThat((string.named("planet") + double.named("lat") + double.named("lon")).monitoredPath("mars", 20.0, 50.5),
            equalTo("/{planet}/{lat}/{lon}"))
        
        assertThat((string.named("planet").monitored() + double.named("lat") + double.named("lon")).monitoredPath("earth", 19.3, 33.5),
            equalTo("/earth/{lat}/{lon}"))
    }
    
    @Test
    fun `monitored path reporting with composition with Empty`() {
        assertThat((root + string.named("planet")).monitoredPath("earth"), equalTo("/{planet}"))
        assertThat((root + "planet" + string.named("planet") + "orbit").monitoredPath("mercury"), equalTo("/planet/{planet}/orbit"))
    }
    
    @Test
    fun `monitored path reporting with restriction`() {
        val path = int.named("x") where { it > 0 }
        
        assertThat(path.monitoredPath(10), equalTo("/{x}"))
    }
    
    @Test
    fun `monitored path with projection`() {
        val planetaryPosition = string.named("planet").monitored() + (double.named("lat") + double.named("lon") asA position)
        
        assertThat(planetaryPosition.monitoredPath("venus", Position(35.2, 16.4)), equalTo("/venus/{lat}/{lon}"))
    }
    
    data class Position(val x: Double, val y: Double)
    
    private val position = projection(
        fromParts = ::Position.forHStacks(),
        toParts = { p -> Empty + p.x + p.y }
    )
}
