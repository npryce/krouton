package com.natpryce.krouton

operator fun <T> String.div(rest: UrlScheme<T>) : UrlScheme<T> = PrefixedUrlScheme(this, rest)

operator fun <T> UrlScheme<T>.div(suffix: String) : UrlScheme<T> = SuffixedUrlScheme(this, suffix)

operator fun <T,U> UrlScheme<T>.div(rest: UrlScheme<U>) : UrlScheme<Pair<T,U>> = AppendedUrlScheme(this, rest)

infix fun <T1,U> UrlScheme<T1>.asA(mapping: Abstraction1<T1,U>): UrlScheme<U> = Abstraction1UrlScheme<T1,U>(this, mapping)
infix fun <T1,T2,U> UrlScheme<Pair<T1,T2>>.asA(mapping: Abstraction2<T1,T2,U>): UrlScheme<U> = Abstraction2UrlScheme<T1,T2,U>(this, mapping)

