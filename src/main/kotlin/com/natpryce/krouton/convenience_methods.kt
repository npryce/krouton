package com.natpryce.krouton

typealias UrlScheme1<A> = UrlScheme<HStack1<A>>
typealias UrlScheme2<A,B> = UrlScheme<HStack2<A,B>>
typealias UrlScheme3<A,B,C> = UrlScheme<HStack3<A,B,C>>
typealias UrlScheme4<A,B,C,D> = UrlScheme<HStack4<A,B,C,D>>
typealias UrlScheme5<A,B,C,D,E> = UrlScheme<HStack5<A,B,C,D,E>>
typealias UrlScheme6<A,B,C,D,E,F> = UrlScheme<HStack6<A,B,C,D,E,F>>

@JvmName("path1")
fun <A> UrlScheme1<A>.path(a: A) =
    path(Empty + a)

@JvmName("path2")
fun <A, B> UrlScheme2<B, A>.path(a: A, b: B) =
    path(Empty + a + b)

@JvmName("path3")
fun <A, B, C> UrlScheme3<C, B, A>.path(a: A, b: B, c: C) =
    path(Empty + a + b + c)

@JvmName("path4")
fun <A, B, C, D> UrlScheme4<D, C, B, A>.path(a: A, b: B, c: C, d: D) =
    path(Empty + a + b + c + d)

@JvmName("pathHStack5")
fun <A, B, C, D, E> UrlScheme5<E, D, C, B, A>.path(a: A, b: B, c: C, d: D, e: E) =
    path(Empty + a + b + c + d + e)

@JvmName("pathHStack6")
fun <A, B, C, D, E, F> UrlScheme6<F, E, D, C, B, A>.path(a: A, b: B, c: C, d: D, e: E, f: F) =
    path(Empty + a + b + c + d + e + f)


@JvmName("monitoredPath1")
fun <A> UrlScheme1<A>.monitoredPath(a: A) =
    monitoredPath(Empty + a)

@JvmName("monitoredPath2")
fun <A, B> UrlScheme2<B, A>.monitoredPath(a: A, b: B) =
    monitoredPath(Empty + a + b)

@JvmName("monitoredPath3")
fun <A, B, C> UrlScheme3<C, B, A>.monitoredPath(a: A, b: B, c: C) =
    monitoredPath(Empty + a + b + c)

@JvmName("monitoredPath4")
fun <A, B, C, D> UrlScheme4<D, C, B, A>.monitoredPath(a: A, b: B, c: C, d: D) =
    monitoredPath(Empty + a + b + c + d)

@JvmName("monitoredPath5")
fun <A, B, C, D, E> UrlScheme5<E, D, C, B, A>.monitoredPath(a: A, b: B, c: C, d: D, e: E) =
    monitoredPath(Empty + a + b + c + d + e)

@JvmName("monitoredPathHStack6")
fun <A, B, C, D, E, F> UrlScheme6<F, E, D, C, B, A>.monitoredPath(a: A, b: B, c: C, d: D, e: E, f: F) =
    monitoredPath(Empty + a + b + c + d + e + f)
