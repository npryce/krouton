

Route Operations:

* Parsing: `Route<T>.parse(String) -> T?`
* Construct: `Route<T>.path(T) -> String`


Route Algebra:

* Composition: `Route<T> / Route<U> -> Route<(T,U)>`
* Prefixion: `String / Route<T> -> Route<T>`
* Suffixion: `Route<T> / String -> Route<T>`
* Restriction: `Route<T> when (T->Boolean) -> Route<T>`
* Projection: `Route<T> asA (T<->S?) -> Route<S>` 


Opinionated:

* Mandatory aspects of a resource locator go in the path
* All query parameters are optional

