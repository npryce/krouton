package com.natpryce.krouton

typealias PathTemplate0 = PathTemplate<Empty>
typealias PathTemplate1<A> = PathTemplate<HStack1<A>>
typealias PathTemplate2<A, B> = PathTemplate<HStack2<B, A>>
typealias PathTemplate3<A, B, C> = PathTemplate<HStack3<C, B, A>>
typealias PathTemplate4<A, B, C, D> = PathTemplate<HStack4<D, C, B, A>>
typealias PathTemplate5<A, B, C, D, E> = PathTemplate<HStack5<E, D, C, B, A>>
typealias PathTemplate6<A, B, C, D, E, F> = PathTemplate<HStack6<F, E, D, C, B, A>>

fun PathTemplate0.path() =
    path(Empty)

fun <A> PathTemplate1<A>.path(a: A) =
    path(Empty + a)

fun <A, B> PathTemplate2<A,B>.path(a: A, b: B) =
    path(Empty + a + b)

fun <A, B, C> PathTemplate3<A, B, C>.path(a: A, b: B, c: C) =
    path(Empty + a + b + c)

fun <A, B, C, D> PathTemplate4<A, B, C, D>.path(a: A, b: B, c: C, d: D) =
    path(Empty + a + b + c + d)

fun <A, B, C, D, E> PathTemplate5<A, B, C, D, E>.path(a: A, b: B, c: C, d: D, e: E) =
    path(Empty + a + b + c + d + e)

fun <A, B, C, D, E, F> PathTemplate6<A, B, C, D, E, F>.path(a: A, b: B, c: C, d: D, e: E, f: F) =
    path(Empty + a + b + c + d + e + f)


fun PathTemplate0.monitoredPath() =
    monitoredPath(Empty)

fun <A> PathTemplate1<A>.monitoredPath(a: A) =
    monitoredPath(Empty + a)

fun <A, B> PathTemplate2<A, B>.monitoredPath(a: A, b: B) =
    monitoredPath(Empty + a + b)

fun <A, B, C> PathTemplate3<A, B, C>.monitoredPath(a: A, b: B, c: C) =
    monitoredPath(Empty + a + b + c)

fun <A, B, C, D> PathTemplate4<A, B, C, D>.monitoredPath(a: A, b: B, c: C, d: D) =
    monitoredPath(Empty + a + b + c + d)

fun <A, B, C, D, E> PathTemplate5<A, B, C, D, E>.monitoredPath(a: A, b: B, c: C, d: D, e: E) =
    monitoredPath(Empty + a + b + c + d + e)

fun <A, B, C, D, E, F> PathTemplate6<A, B, C, D, E, F>.monitoredPath(a: A, b: B, c: C, d: D, e: E, f: F) =
    monitoredPath(Empty + a + b + c + d + e + f)
