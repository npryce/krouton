package com.natpryce.krouton

operator fun <T> String.div(rest: UrlScheme<T>) : UrlScheme<T> = PrefixedUrlScheme(this, rest)
operator fun <T> UrlScheme<T>.div(suffix: String) : UrlScheme<T> = SuffixedUrlScheme(this, suffix)
operator fun <T,U> UrlScheme<T>.div(rest: UrlScheme<U>) : UrlScheme<Pair<T,U>> = AppendedUrlScheme(this, rest)
