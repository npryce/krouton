# Krouton


Typesafe, compositional routing and reverse routing for web apps and HTTP microservices for Kotlin.

[![Kotlin](https://img.shields.io/badge/kotlin-1.0.0-blue.svg)](http://kotlinlang.org)
[![Build Status](https://travis-ci.org/npryce/konfig.svg?branch=master)](https://travis-ci.org/npryce/konfig)
[![Maven Central](https://img.shields.io/maven-central/v/com.natpryce/konfig.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.natpryce%22%20AND%20a%3A%22konfig%22)


## Examples

 * [Simple routing within the Sun JDK HttpServer](src/test/kotlin/com/natpryce/krouton/example/HttpRoutingExample.kt)
 * [Routing by path and method, using Kotlin's `when` expression](src/test/kotlin/com/natpryce/krouton/example/CountersExample.kt)

## Principles

Type safe routing and reverse routing.

No reflection, annotations or classpath scanning.

Separate reactive code from routing policy

* Routing policy defined by constants (immutable objects)
* Routing done by functions/closures/objects

Compositional: routes are composed from primitive parts, and user-defined routes can be used in 
exactly the same way as the predefined primitives.

Can be used with any HTTP server library.

## Routing Policy Operations

* Parsing: `UrlScheme<T>.parse(String) -> T?`
* Reverse Routing: `UrlScheme<T>.path(T) -> String`


## Route Algebra

* Composition: 
    * `<T,U> UrlScheme<T> / UrlScheme<U> -> UrlScheme<(T,U)>`
    * `<T> UrlScheme<Unit> / UrlScheme<T> -> UrlScheme<T>`
    * `<T> UrlScheme<T> / UrlScheme<Unit> -> UrlScheme<T>`
* Prefixion: `<T> String / UrlScheme<T> -> UrlScheme<T>`
* Suffixion: `<T> UrlScheme<T> / String -> UrlScheme<T>`
* Restriction: `<T> UrlScheme<T> where ((T)->Boolean) -> UrlScheme<T>`
* Projection: `<T,U> UrlScheme<T> asA Projection<T,U> -> UrlScheme<U>`


## Opinionated

* Mandatory aspects of a resource locator go in the path
* All query parameters are optional

