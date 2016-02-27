package com.natpryce.krouton

operator fun <T> String.div(rest: UrlScheme<T>) : UrlScheme<T> = PrefixedUrlScheme(this, rest)

operator fun <T> UrlScheme<T>.div(suffix: String) : UrlScheme<T> = SuffixedUrlScheme(this, suffix)

operator fun <T,U> UrlScheme<T>.div(rest: UrlScheme<U>) : UrlScheme<Pair<T,U>> = AppendedUrlScheme(this, rest)

infix fun <T> UrlScheme<T>.where(p: (T)->Boolean): UrlScheme<T> = RestrictedUrlScheme<T>(this, p)

infix fun <T1,U> UrlScheme<T1>.asA(projection: Projection1<T1,U>): UrlScheme<U> =
        Projection1UrlScheme<T1,U>(this, projection)

infix fun <T1,T2,U> UrlScheme<Pair<T1,T2>>.asA(projection: Projection2<T1,T2,U>): UrlScheme<U> =
        Projection2UrlScheme<T1,T2,U>(this, projection)

infix fun <T1,T2,T3,U> UrlScheme<Pair<Pair<T1,T2>,T3>>.asA(projection: Projection3<T1,T2,T3,U>): UrlScheme<U> =
        Projection3UrlScheme<T1,T2,T3,U>(this, projection)

infix fun <T1,T2,T3,T4,U> UrlScheme<Pair<Pair<Pair<T1,T2>,T3>,T4>>.asA(projection: Projection4<T1,T2,T3,T4,U>): UrlScheme<U> =
        Projection4UrlScheme<T1,T2,T3,T4,U>(this, projection)
