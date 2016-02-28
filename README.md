Typesafe, compositional routing and reverse routing for web apps and HTTP microservices


## Principles

Type safe routing and reverse routing.

No reflection, annotations or classpath scanning.

Separate reactive code from routing policy

* Routing policy defined by constants (immutable objects)
* Routing done by functions/closures/objects

Compositional: routes are composed from primitive parts, and user-defined routes can be used in exactly the same way as the predefined primitives.


## Routing Policy Operations

* Parsing: `UrlScheme<T>.parse(String) -> T?`
* Reverse Routing: `UrlScheme<T>.path(T) -> String`


## Route Algebra

* Composition: `UrlScheme<T> / UrlScheme<U> -> UrlScheme<(T,U)>`
* Prefixion: `String / UrlScheme<T> -> UrlScheme<T>`
* Suffixion: `UrlScheme<T> / String -> UrlScheme<T>`
* Restriction: `UrlScheme<T> where ((T)->Boolean) -> UrlScheme<T>`
* Projection: `UrlScheme<T> asA Mapping<T,U> -> UrlScheme<U>` 


## Opinionated

* Mandatory aspects of a resource locator go in the path
* All query parameters are optional

