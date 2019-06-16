package com.natpryce.krouton

import java.net.URI


fun URI.resolve(route: PathTemplate0): URI =
    resolve(route.path())

fun <T1> URI.resolve(route: PathTemplate1<T1>, p1: T1): URI =
    resolve(route.path(p1))

fun <T1, T2> URI.resolve(route: PathTemplate2<T1, T2>, p1: T1, p2: T2): URI =
    resolve(route.path(p1, p2))

fun <T1, T2, T3> URI.resolve(route: PathTemplate3<T1, T2, T3>, p1: T1, p2: T2, p3: T3): URI =
    resolve(route.path(p1, p2, p3))

fun <T1, T2, T3, T4> URI.resolve(route: PathTemplate4<T1, T2, T3, T4>, p1: T1, p2: T2, p3: T3, p4: T4): URI =
    resolve(route.path(p1, p2, p3, p4))


fun URI.extend(route: PathTemplate0): URI =
    extend(route.path())

fun <T1> URI.extend(route: PathTemplate1<T1>, p1: T1): URI =
    extend(route.path(p1))

fun <T1, T2> URI.extend(route: PathTemplate2<T1, T2>, p1: T1, p2: T2): URI =
    extend(route.path(p1, p2))

fun <T1, T2, T3> URI.extend(route: PathTemplate3<T1, T2, T3>, p1: T1, p2: T2, p3: T3): URI =
    extend(route.path(p1, p2, p3))

fun <T1, T2, T3, T4> URI.extend(route: PathTemplate4<T1, T2, T3, T4>, p1: T1, p2: T2, p3: T3, p4: T4): URI =
    extend(route.path(p1, p2, p3, p4))

fun URI.extend(pathExtension: String) =
    resolve(path.removeSuffix("/") + pathExtension)
