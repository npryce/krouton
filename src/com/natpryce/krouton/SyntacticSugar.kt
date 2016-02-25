package com.natpryce.krouton

operator fun <T> String.div(rest: UrlScheme<T>) : UrlScheme<T> = PrefixedUrlScheme(this, rest)

operator fun <T> UrlScheme<T>.div(suffix: String) : UrlScheme<T> = SuffixedUrlScheme(this, suffix)

operator fun <T,U> UrlScheme<T>.div(rest: UrlScheme<U>) : UrlScheme<Pair<T,U>> = AppendedUrlScheme(this, rest)

infix fun <T> UrlScheme<T>.where(p: (T)->Boolean): UrlScheme<T> = RestrictedUrlScheme<T>(this, p)

infix fun <T1,U> UrlScheme<T1>.asA(mapping: Has1Part<T1,U>): UrlScheme<U> =
        Has1PartUrlScheme<T1,U>(this, mapping)

infix fun <T1,T2,U> UrlScheme<Pair<T1,T2>>.asA(mapping: Has2Parts<T1,T2,U>): UrlScheme<U> =
        Has2PartsUrlScheme<T1,T2,U>(this, mapping)

infix fun <T1,T2,T3,U> UrlScheme<Pair<Pair<T1,T2>,T3>>.asA(mapping: Has3Parts<T1,T2,T3,U>): UrlScheme<U> =
        Has3PartsUrlScheme<T1,T2,T3,U>(this, mapping)

infix fun <T1,T2,T3,T4,U> UrlScheme<Pair<Pair<Pair<T1,T2>,T3>,T4>>.asA(mapping: Has4Parts<T1,T2,T3,T4,U>): UrlScheme<U> =
        Has4PartsUrlScheme<T1,T2,T3,T4,U>(this, mapping)
