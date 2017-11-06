package com.natpryce.krouton

operator fun String.unaryPlus() = root + this
operator fun <T> PathTemplate<T>.unaryPlus() = root + this

operator fun <T> PathTemplate<T>.plus(fixedElement: String): PathTemplate<T> =
    this + LiteralPathElement(fixedElement)

@JvmName("append")
operator fun <T : HStack, U> PathTemplate<T>.plus(rest: PathTemplate<U>): PathTemplate<HCons<U, T>> = AppendedPathTemplate(this, rest)

@JvmName("plusPrefix")
operator fun <T> PathTemplate<Empty>.plus(rest: PathTemplate<T>): PathTemplate<T> =
    PrefixedPathTemplate(this, rest)

@JvmName("plusSuffix")
operator fun <T> PathTemplate<T>.plus(suffix: PathTemplate<Empty>): PathTemplate<T> =
    SuffixedPathTemplate(this, suffix)

operator fun <T, U> PathTemplate<T>.plus(rest: PathTemplate<U>): PathTemplate<HStack2<U, T>> =
    AppendedPathTemplate(this asA tStack(), rest)

infix fun <T> PathTemplate<T>.where(p: (T) -> Boolean): PathTemplate<T> = RestrictedPathTemplate(this, p)

infix fun <Parts, Mapped> PathTemplate<Parts>.asA(projection: Projection<Parts, Mapped>): PathTemplate<Mapped> =
    ProjectedPathTemplate(this, projection)
