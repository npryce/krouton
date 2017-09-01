package com.natpryce.krouton

operator fun <T> UrlScheme<T>.plus(fixedElement: String): UrlScheme<T> =
    this + FixedPathElement(fixedElement)

@JvmName("plusToTStack")
operator fun <T : HStack, U> UrlScheme<T>.plus(rest: UrlScheme<U>): UrlScheme<HCons<U, T>> = AppendedUrlScheme(this, rest)

@JvmName("plusPrefix")
operator fun <T> UrlScheme<Empty>.plus(rest: UrlScheme<T>): UrlScheme<T> =
    object : UrlScheme<T> {
        override fun pathElementsFrom(value: T) =
            this@plus.pathElementsFrom(Empty) + rest.pathElementsFrom(value)
        
        override fun parsePathElements(pathElements: List<String>) =
            this@plus.parsePathElements(pathElements)
                ?.let { (_, restPathElements) -> rest.parsePathElements(restPathElements) }
    }

@JvmName("plusSuffix")
operator fun <T> UrlScheme<T>.plus(rest: UrlScheme<Empty>): UrlScheme<T> =
    object : UrlScheme<T> {
        override fun pathElementsFrom(value: T) =
            this@plus.pathElementsFrom(value) + rest.pathElementsFrom(Empty)
    
        override fun parsePathElements(pathElements: List<String>) =
            this@plus.parsePathElements(pathElements)
                ?.let { (value, restPathElements) -> rest.parsePathElements(restPathElements)
                    ?.let { (_, remainderPathElements) -> Pair(value, remainderPathElements) }
                }
    }

@JvmName("plusRaw")
operator fun <T,U> UrlScheme<T>.plus(rest: UrlScheme<U>): UrlScheme<HStack2<U,T>> =
    AppendedUrlScheme(this asA tStack(), rest)

infix fun <T> UrlScheme<T>.where(p: (T) -> Boolean): UrlScheme<T> = RestrictedUrlScheme<T>(this, p)

infix fun <Parts, Mapped> UrlScheme<Parts>.asA(projection: Projection<Parts, Mapped>): UrlScheme<Mapped> =
    ProjectionUrlScheme<Parts, Mapped>(this, projection)

operator fun <T> UrlScheme<T>.unaryPlus(): UrlScheme<List<T>> = repeated()

fun <T> UrlScheme<T>.repeated() = RepeatedUrlScheme(this)
