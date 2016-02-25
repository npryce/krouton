

Routing Operations:

* Parsing: `UrlScheme<T>.parse(String) -> T?`
* Reverse Routing: `UrlScheme<T>.path(T) -> String`


Route Algebra:

* Composition: `UrlScheme<T> / Route<U> -> UrlScheme<(T,U)>`
* Prefixion: `String / UrlScheme<T> -> UrlScheme<T>`
* Suffixion: `UrlScheme<T> / String -> UrlScheme<T>`
* Restriction: `UrlScheme<T> when (T)->Boolean -> UrlScheme<T>`
* Projection: `UrlScheme<T> asA Mapping<T,U> -> UrlScheme<U>` 


Opinionated:

* Mandatory aspects of a resource locator go in the path
* All query parameters are optional

