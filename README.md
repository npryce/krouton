# Krouton


Typesafe, compositional routing and reverse routing for [Kotlin](https://kotlinlang.org) web apps and HTTP microservices.

[![Kotlin](https://img.shields.io/badge/kotlin-1.2.51-blue.svg)](http://kotlinlang.org)
[![Build Status](https://travis-ci.org/npryce/krouton.svg?branch=master)](https://travis-ci.org/npryce/krouton)
[![Maven Central](https://img.shields.io/maven-central/v/com.natpryce/krouton.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.natpryce%22%20AND%20a%3A%22krouton%22)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http//www.apache.org/licenses/LICENSE-2.0)

Krouton provides a routing API for [HTTP4K](https://http4k.org), but the core abstractions can be used with any web server library.

## Examples

 * [Routing and reverse routing with HTTP4K](src/test/kotlin/com/natpryce/krouton/example/HttpRoutingExample.kt)

## Principles

Type safe routing and reverse routing.

No reflection, annotations or classpath scanning.

Explicit, type-checked flow of data and control, instead of "spooky action at a distance" via reflection, annotations,
classpath scanning, passing data in context maps or synthetic HTTP headers, or control flow via exceptions.  

Separate code that routes and handles requests from definitions of URLs

* URLs defined by constants (immutable objects)
* Routing policy defined by operations on those constants
* Routing done by functions/closures/objects that connect Krouton's routing policy API to a web server library.

Compositional: routes are composed from primitive parts and composition operators. User-defined routes can be used in 
exactly the same way as the predefined primitives.

Mandatory aspects of a resource locator go in the path

Query parameters are optional and are interpreted by the resource.


## Routing policy operations

* Parsing: `UrlScheme<T>.parse(String) -> T?`
* Reverse Routing: `UrlScheme<T>.path(T) -> String`
* Reporting: `UrlScheme<T>.monitoredPath(T)-> String`

## Route composition

* Append: 
    * `UrlScheme<T> + UrlScheme<U> -> UrlScheme<TCons<T,U>>`
    * `UrlScheme<Empty> + UrlScheme<T> -> UrlScheme<T>`
    * `UrlScheme<T> + UrlScheme<Empty> -> UrlScheme<T>`
* Append fixed path element: `UrlScheme<T> + String -> UrlScheme<T>`
* Restrict: `UrlScheme<T> where ((T)->Boolean) -> UrlScheme<T>`
* Project: `UrlScheme<T> asA Projection<T,U> -> UrlScheme<U>`

Krouton includes an _HStack_ type that represents heterogenous stacks whose size and element types are known at compile-time.  This means that the type parameter of a `UrlScheme<T>` can represent _multiple_ typed values, parsed from different path elements.

## What's with the version number?

The version number is {mental}.{major}.{minor}.{patch}.  The last three digits are treated as a
[semantic version number](https://semver.org).  The first digit is incremented if there is a significant 
change in the mental model underpinning the library.   A major version of zero always signifies a pre-release version,
irrespective of the value of the first digit.  The API of pre-release versions may go through significant changes in 
response to user feedback before the release of version x.1.0.0.
