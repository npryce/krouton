# Krouton


Typesafe, compositional routing and reverse routing for [Kotlin](https://kotlinlang.org) web apps and HTTP microservices.

[![Kotlin](https://img.shields.io/badge/kotlin-1.1.4-blue.svg)](http://kotlinlang.org)
[![Build Status](https://travis-ci.org/npryce/krouton.svg?branch=master)](https://travis-ci.org/npryce/krouton)
[![Maven Central](https://img.shields.io/maven-central/v/com.natpryce/krouton.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.natpryce%22%20AND%20a%3A%22krouton%22)

Krouton provides a routing API for [HTTP4K](https://http4k.org), but the core abstractions can be used with any web server library.

## Examples

 * [Routing and reverse routing with HTTP4K](src/test/kotlin/com/natpryce/krouton/example/HttpRoutingExample.kt)

## Principles

Type safe routing and reverse routing.

No reflection, annotations or classpath scanning.

Separate reactive code from routing policy

* Routing policy defined by constants (immutable objects)
* Routing done by functions/closures/objects

Compositional: routes are composed from primitive parts, and user-defined routes can be used in 
exactly the same way as the predefined primitives.

## Routing Policy Operations

* Parsing: `UrlScheme<T>.parse(String) -> T?`
* Reverse Routing: `UrlScheme<T>.path(T) -> String`


## Route Composition

* Append: 
    * `UrlScheme<T> + UrlScheme<U> -> UrlScheme<(T,U)>`
    * `UrlScheme<Empty> / UrlScheme<T> -> UrlScheme<T>`
    * `UrlScheme<T> / UrlScheme<Empty> -> UrlScheme<T>`
* Prefix: `root + String + UrlScheme<T> -> UrlScheme<T>`
* Suffix: `UrlScheme<T> + String -> UrlScheme<T>`
* Restrict: `UrlScheme<T> where ((T)->Boolean) -> UrlScheme<T>`
* Project: `UrlScheme<T> asA Projection<T,U> -> UrlScheme<U>`

## Opinionated

* Mandatory aspects of a resource locator go in the path
* All query parameters are optional

