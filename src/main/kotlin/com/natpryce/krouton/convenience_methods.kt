package com.natpryce.krouton

typealias PathTemplate1<A> = PathTemplate<HStack1<A>>
typealias PathTemplate2<A,B> = PathTemplate<HStack2<A,B>>
typealias PathTemplate3<A,B,C> = PathTemplate<HStack3<A,B,C>>
typealias PathTemplate4<A,B,C,D> = PathTemplate<HStack4<A,B,C,D>>
typealias PathTemplate5<A,B,C,D,E> = PathTemplate<HStack5<A,B,C,D,E>>
typealias PathTemplate6<A,B,C,D,E,F> = PathTemplate<HStack6<A,B,C,D,E,F>>

fun PathTemplate<Empty>.path() =
    path(Empty)

fun <A> PathTemplate1<A>.path(a: A) =
    path(Empty + a)

fun <A, B> PathTemplate2<B, A>.path(a: A, b: B) =
    path(Empty + a + b)

fun <A, B, C> PathTemplate3<C, B, A>.path(a: A, b: B, c: C) =
    path(Empty + a + b + c)

fun <A, B, C, D> PathTemplate4<D, C, B, A>.path(a: A, b: B, c: C, d: D) =
    path(Empty + a + b + c + d)

fun <A, B, C, D, E> PathTemplate5<E, D, C, B, A>.path(a: A, b: B, c: C, d: D, e: E) =
    path(Empty + a + b + c + d + e)

fun <A, B, C, D, E, F> PathTemplate6<F, E, D, C, B, A>.path(a: A, b: B, c: C, d: D, e: E, f: F) =
    path(Empty + a + b + c + d + e + f)


fun <A> PathTemplate1<A>.monitoredPath(a: A) =
    monitoredPath(Empty + a)

fun <A, B> PathTemplate2<B, A>.monitoredPath(a: A, b: B) =
    monitoredPath(Empty + a + b)

fun <A, B, C> PathTemplate3<C, B, A>.monitoredPath(a: A, b: B, c: C) =
    monitoredPath(Empty + a + b + c)

fun <A, B, C, D> PathTemplate4<D, C, B, A>.monitoredPath(a: A, b: B, c: C, d: D) =
    monitoredPath(Empty + a + b + c + d)

fun <A, B, C, D, E> PathTemplate5<E, D, C, B, A>.monitoredPath(a: A, b: B, c: C, d: D, e: E) =
    monitoredPath(Empty + a + b + c + d + e)

fun <A, B, C, D, E, F> PathTemplate6<F, E, D, C, B, A>.monitoredPath(a: A, b: B, c: C, d: D, e: E, f: F) =
    monitoredPath(Empty + a + b + c + d + e + f)
