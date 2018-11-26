package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.describe
import com.natpryce.hamkrest.equalTo
import com.oneeyedmen.minutest.Context
import com.oneeyedmen.minutest.TestContext
import com.oneeyedmen.minutest.experimental.context
import com.oneeyedmen.minutest.junit.JupiterTests
import com.oneeyedmen.minutest.junit.context
import java.time.LocalDate
import java.util.Locale


val `single element routes` = context<Unit> {
    behaviourOf(string,
        routing = listOf("/foo" to "foo"),
        invalidPaths = listOf("/", "/foo/bar"))
    
    behaviourOf(int,
        routing = listOf("/0" to 0, "/1" to 1, "/-1" to -1, "/-4" to -4),
        forwardRouting = listOf("/010" to 10),
        reverseRouting = listOf(10 to "/10"),
        invalidPaths = listOf("/", "/foo"))
    
    behaviourOf(double,
        routing = listOf("/1.0" to 1.0, "/-2.0" to -2.0, "/1.5" to 1.5, "/-3.25" to -3.25),
        forwardRouting = listOf("/1" to 1.0),
        invalidPaths = listOf("/", "/foo"))
    
    behaviourOf(isoLocalDate,
        routing = listOf("/2016-02-25" to LocalDate.of(2016, 2, 25)),
        invalidPaths = listOf("/2016-XX-88", "/foo"))
    
    behaviourOf(axis, name = "enums",
        routing = listOf("/X" to Axis.X, "/Y" to Axis.Y, "/Z" to Axis.Z),
        invalidPaths = listOf("/", "/x", "/a", "/ddd"))
    
    behaviourOf(locale,
        routing = listOf("/fr-FR" to Locale.FRANCE, "/fr-CA" to Locale.CANADA_FRENCH, "/fr" to Locale.FRENCH),
        forwardRouting = listOf("/fr-" to Locale.FRENCH, "/FR-FR" to Locale.FRANCE),
        invalidPaths = listOf("/"))
}

private enum class Axis { X, Y, Z }

private val axis: VariablePathElement<Axis> by enum()


inline fun <reified T> TestContext<Unit>.behaviourOf(
    template: PathTemplate<T>,
    name: String = T::class.simpleName ?: template.toUrlTemplate(),
    routing: List<Pair<String, T>> = emptyList(),
    forwardRouting: List<Pair<String, T>> = emptyList(),
    reverseRouting: List<Pair<T, String>> = emptyList(),
    invalidPaths: List<String> = emptyList()
) {
    testContextFor(name, template, routing, forwardRouting, reverseRouting, invalidPaths)
}

fun <T> TestContext<Unit>.testContextFor(name: String, template: PathTemplate<T>, routing: List<Pair<String, T>>, forwardRouting: List<Pair<String, T>>, reverseRouting: List<Pair<T, String>>, invalidPaths: List<String>) {
    derivedContext<PathTemplate<T>>(name) {
        fixture { template }
        
        context("parsing valid paths to values") {
            (routing + forwardRouting).forEach { (validPath, value) ->
                testParse(validPath, value)
            }
        }
        
        context("reverse routing: generating paths from values") {
            (routing + reverseRouting.flipAll()).forEach { (validPath, value) ->
                testToPath(value, validPath)
            }
        }
        
        context("parsing invalid paths") {
            invalidPaths.forEach {
                test("$it is an invalid path") {
                    assertThat(parse(it), absent())
                }
            }
        }
    }
}


private fun <T, U> Iterable<Pair<T, U>>.flipAll(): List<Pair<U, T>> = map { it.second to it.first }

private fun <T> Context<PathTemplate<T>, PathTemplate<T>>.testToPath(value: T, validPath: String) {
    test("${describe(value)} generates the path $validPath") {
        assertThat(path(value), equalTo(validPath))
    }
}

private fun <T> Context<PathTemplate<T>, PathTemplate<T>>.testParse(validPath: String, value: T) {
    test("$validPath parses to ${describe(value)}") {
        assertThat(parse(validPath), equalTo(value))
    }
}
