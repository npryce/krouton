package com.natpryce.krouton

operator fun <T> String.div(rest: UrlScheme<T>) : UrlScheme<T> = PrefixedUrlScheme(this, rest)

operator fun <T> UrlScheme<T>.div(suffix: String) : UrlScheme<T> = SuffixedUrlScheme(this, suffix)

operator fun <T,U> UrlScheme<T>.div(rest: UrlScheme<U>) : UrlScheme<Pair<T,U>> = AppendedUrlScheme(this, rest)

infix fun <T1,U> UrlScheme<T1>.asA(mapping: Has1Part<T1,U>): UrlScheme<U> = Has1PartUrlScheme<T1,U>(this, mapping)
infix fun <T1,T2,U> UrlScheme<Pair<T1,T2>>.asA(mapping: Has2Parts<T1,T2,U>): UrlScheme<U> = Has2PartsUrlScheme<T1,T2,U>(this, mapping)

