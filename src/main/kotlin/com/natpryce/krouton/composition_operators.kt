package com.natpryce.krouton

operator fun String.unaryPlus() = root + this
operator fun <T> PathTemplate<T>.unaryPlus() = root + this

operator fun <T> PathTemplate<T>.plus(fixedElement: String): PathTemplate<T> =
    this + LiteralPathElement(fixedElement)

@JvmName("plusPrefix")
operator fun <T> PathTemplate<Unit>.plus(rest: PathTemplate<T>): PathTemplate<T> =
    PrefixedPathTemplate(this, rest)

@JvmName("plusSuffix")
operator fun <T> PathTemplate<T>.plus(suffix: PathTemplate<Unit>): PathTemplate<T> =
    SuffixedPathTemplate(this, suffix)


infix fun <T> PathTemplate<T>.where(p: (T) -> Boolean): PathTemplate<T> = RestrictedPathTemplate(this, p)

infix fun <Parts, Mapped> PathTemplate<Parts>.asA(projection: Projection<Parts, Mapped>): PathTemplate<Mapped> =
    ProjectedPathTemplate(this, projection)


private fun <T, U, V> appender(tPath: PathTemplate<T>, uPath: PathTemplate<U>, fromParts: (Tuple2<T, U>) -> V, toParts: (V) -> Tuple2<T, U>) =
    AppendedPathTemplate(tPath, uPath) asA object : Projection<Tuple2<T, U>, V> {
        override fun fromParts(parts: Tuple2<T, U>): V = fromParts(parts)
        override fun toParts(mapped: V): Tuple2<T, U> = toParts(mapped)
    }


@JvmName("appendScalarScalar")
operator fun <T, U> PathTemplate<T>.plus(rest: PathTemplate<U>): PathTemplate<Tuple2<T, U>> =
    AppendedPathTemplate(this, rest)

@JvmName("appendScalarTuple2")
operator fun <T1, T2, T3> PathElement<T1>.plus(rest: PathTemplate<Tuple2<T2, T3>>) =
    appender(this, rest,
        fromParts = { tupleFlat(it.val1, it.val2) },
        toParts = { tuple(it.val1, tuple(it.val2, it.val3)) })

@JvmName("appendScalarTuple3")
operator fun <T1, T2, T3, T4> PathElement<T1>.plus(rest: PathTemplate<Tuple3<T2, T3, T4>>) =
    appender(this, rest,
        fromParts = { tupleFlat(it.val1, it.val2) },
        toParts = { tuple(it.val1, tuple(it.val2, it.val3, it.val4)) })

@JvmName("appendScalarTuple4")
operator fun <T1, T2, T3, T4, T5> PathElement<T1>.plus(rest: PathTemplate<Tuple4<T2, T3, T4, T5>>) =
    appender(this, rest,
        fromParts = { tupleFlat(it.val1, it.val2) },
        toParts = { tuple(it.val1, tuple(it.val2, it.val3, it.val4, it.val5)) })


@JvmName("appendTuple2Scalar")
operator fun <T1, T2, T3> PathTemplate<Tuple2<T1, T2>>.plus(rest: PathTemplate<T3>) =
    appender(this, rest, { tupleFlat(it.val1, it.val2) }, { tuple(tuple(it.val1, it.val2), it.val3) })

@JvmName("appendTuple2Tuple2")
operator fun <T1, T2, T3, T4> PathTemplate<Tuple2<T1, T2>>.plus(rest: PathTemplate<Tuple2<T3, T4>>) =
    appender(this, rest, { tupleFlat(it.val1, it.val2) }, { tuple(tuple(it.val1, it.val2), tuple(it.val3, it.val4)) })

@JvmName("appendTuple2Tuple3")
operator fun <T1, T2, T3, T4, T5> PathTemplate<Tuple2<T1, T2>>.plus(rest: PathTemplate<Tuple3<T3, T4, T5>>) =
    appender(this, rest, { tupleFlat(it.val1, it.val2) }, { tuple(tuple(it.val1, it.val2), tuple(it.val3, it.val4, it.val5)) })


@JvmName("appendTuple3Scalar")
operator fun <T1, T2, T3, T4> PathTemplate<Tuple3<T1, T2, T3>>.plus(rest: PathTemplate<T4>) =
    appender(this, rest, { tupleFlat(it.val1, it.val2) }, { tuple(tuple(it.val1, it.val2, it.val3), it.val4) })

@JvmName("appendTuple3Tuple2")
operator fun <T1, T2, T3, T4, T5> PathTemplate<Tuple3<T1, T2, T3>>.plus(rest: PathTemplate<Tuple2<T4, T5>>) =
    appender(this, rest, { tupleFlat(it.val1, it.val2) }, { tuple(tuple(it.val1, it.val2, it.val3), tuple(it.val4, it.val5)) })

@JvmName("appendTuple4Scalar")
operator fun <T1, T2, T3, T4, T5> PathTemplate<Tuple4<T1, T2, T3, T4>>.plus(rest: PathTemplate<T5>) =
    appender(this, rest, { tupleFlat(it.val1, it.val2) }, { tuple(tuple(it.val1, it.val2, it.val3, it.val4), it.val5) })
