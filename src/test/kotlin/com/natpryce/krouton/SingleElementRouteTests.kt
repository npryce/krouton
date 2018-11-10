package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.junit.JupiterTests
import com.oneeyedmen.minutest.junit.context
import java.time.LocalDate
import java.util.Locale

class SingleElementRouteTests : JupiterTests {
    enum class Axis {
        X, Y, Z
    }
    
    override val tests = context<Unit> {
        test("strings") {
            assertThat(string.parse("/foo"), equalTo("foo"))
            
            assertThat(string.parse("/"), absent())
            assertThat(string.parse("/foo/bar"), absent())
            
            assertThat(string.path("foo"), equalTo("/foo"))
        }
        
        test("ints") {
            assertThat(int.parse("/1"), equalTo(1))
            assertThat(int.parse("/-4"), equalTo(-4))
            assertThat(int.parse("/010"), equalTo(10))
            
            assertThat(int.parse("/bob"), absent())
            
            assertThat(int.path(1), equalTo("/1"))
            assertThat(int.path(-1), equalTo("/-1"))
        }
        
        test("doubles") {
            assertThat(double.parse("/1"), equalTo(1.0))
            assertThat(double.parse("/1.0"), equalTo(1.0))
            assertThat(double.parse("/-2.0"), equalTo(-2.0))
            
            assertThat(double.parse("/bob"), absent())
            
            assertThat(double.path(1.5), equalTo("/1.5"))
            assertThat(double.path(-3.25), equalTo("/-3.25"))
        }
        
        
        test("iso_dates") {
            assertThat(isoLocalDate.parse("/2016-02-25"), equalTo(LocalDate.of(2016, 2, 25)))
            assertThat(isoLocalDate.parse("/2016-XX-88"), absent())
        }
        
        
        test("enums") {
            val axis: VariablePathElement<Axis> by enum()
            
            assertThat(axis.parse("/X"), equalTo(Axis.X))
            assertThat(axis.parse("/Y"), equalTo(Axis.Y))
            assertThat(axis.parse("/Z"), equalTo(Axis.Z))
            
            assertThat(axis.parse("/x"), absent())
            assertThat(axis.parse("/a"), absent())
            assertThat(axis.parse("/ddd"), absent())
            
            assertThat(axis.path(Axis.Z), equalTo("/Z"))
        }
        
        test("locales") {
            assertThat(locale.parse("/fr-FR"), equalTo(Locale.FRANCE))
            assertThat(locale.path(Locale.FRANCE), equalTo("/fr-FR"))
        }
    }
}