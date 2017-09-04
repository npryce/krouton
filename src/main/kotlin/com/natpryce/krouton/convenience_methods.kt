package com.natpryce.krouton

@JvmName("pathHStack1")
fun <A> UrlScheme<HStack1<A>>.path(a: A) =
    path(Empty + a)

@JvmName("pathHStack2")
fun <A, B> UrlScheme<HStack2<B, A>>.path(a: A, b: B) =
    path(Empty + a + b)

@JvmName("pathHStack3")
fun <A, B, C> UrlScheme<HStack3<C, B, A>>.path(a: A, b: B, c: C) =
    path(Empty + a + b + c)

@JvmName("pathHStack4")
fun <A, B, C, D> UrlScheme<HStack4<D, C, B, A>>.path(a: A, b: B, c: C, d: D) =
    path(Empty + a + b + c + d)

@JvmName("pathHStack5")
fun <A, B, C, D, E> UrlScheme<HStack5<E, D, C, B, A>>.path(a: A, b: B, c: C, d: D, e: E) =
    path(Empty + a + b + c + d + e)

@JvmName("pathHStack6")
fun <A, B, C, D, E, F> UrlScheme<HStack6<F, E, D, C, B, A>>.path(a: A, b: B, c: C, d: D, e: E, f: F) =
    path(Empty + a + b + c + d + e + f)


@JvmName("monitoredPathHStack1")
fun <A> UrlScheme<HStack1<A>>.monitoredPath(a: A) =
    monitoredPath(Empty + a)

@JvmName("monitoredPathHStack2")
fun <A, B> UrlScheme<HStack2<B, A>>.monitoredPath(a: A, b: B) =
    monitoredPath(Empty + a + b)

@JvmName("monitoredPathHStack3")
fun <A, B, C> UrlScheme<HStack3<C, B, A>>.monitoredPath(a: A, b: B, c: C) =
    monitoredPath(Empty + a + b + c)

@JvmName("monitoredPathHStack4")
fun <A, B, C, D> UrlScheme<HStack4<D, C, B, A>>.monitoredPath(a: A, b: B, c: C, d: D) =
    monitoredPath(Empty + a + b + c + d)

@JvmName("monitoredPathHStack5")
fun <A, B, C, D, E> UrlScheme<HStack5<E, D, C, B, A>>.monitoredPath(a: A, b: B, c: C, d: D, e: E) =
    monitoredPath(Empty + a + b + c + d + e)

@JvmName("monitoredPathHStack6")
fun <A, B, C, D, E, F> UrlScheme<HStack6<F, E, D, C, B, A>>.monitoredPath(a: A, b: B, c: C, d: D, e: E, f: F) =
    monitoredPath(Empty + a + b + c + d + e + f)

