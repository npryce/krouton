package com.natpryce.krouton

operator fun String.unaryPlus() = root + this
operator fun <T> UrlScheme<T>.unaryPlus() = root + this

operator fun <T> UrlScheme<T>.plus(fixedElement: String): UrlScheme<T> =
    this + LiteralPathElement(fixedElement)

@JvmName("append")
operator fun <T : HStack, U> UrlScheme<T>.plus(rest: UrlScheme<U>): UrlScheme<HCons<U, T>> = AppendedUrlScheme(this, rest)

@JvmName("plusPrefix")
operator fun <T> UrlScheme<Empty>.plus(rest: UrlScheme<T>): UrlScheme<T> =
    PrefixedUrlScheme(this, rest)

@JvmName("plusSuffix")
operator fun <T> UrlScheme<T>.plus(suffix: UrlScheme<Empty>): UrlScheme<T> =
    SuffixedUrlScheme(this, suffix)

operator fun <T, U> UrlScheme<T>.plus(rest: UrlScheme<U>): UrlScheme<HStack2<U, T>> =
    AppendedUrlScheme(this asA tStack(), rest)

infix fun <T> UrlScheme<T>.where(p: (T) -> Boolean): UrlScheme<T> = RestrictedUrlScheme<T>(this, p)

infix fun <Parts, Mapped> UrlScheme<Parts>.asA(projection: Projection<Parts, Mapped>): UrlScheme<Mapped> =
    ProjectionUrlScheme<Parts, Mapped>(this, projection)
