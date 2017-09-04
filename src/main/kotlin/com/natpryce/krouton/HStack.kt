package com.natpryce.krouton

/**
 * A heterogeneous stack of values of different types that maintains static type and length information
 */
sealed class HStack

/**
 * The empty stack
 */
object Empty : HStack() {
    override fun toString() = "Empty"
}

/**
 * A value pushed onto the TStack
 */
class HCons<out Top, out Rest : HStack>(val top: Top, val rest: Rest) : HStack() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as HCons<*, *>
        
        if (top != other.top) return false
        if (rest != other.rest) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = top?.hashCode() ?: 0
        result = 31 * result + rest.hashCode()
        return result
    }
    
    override fun toString(): String {
        return "$top:$rest"
    }
}

operator fun <Top, Rest : HStack> Rest.plus(top: Top): HCons<Top, Rest> = HCons(top, this)


typealias HStack1<A> = HCons<A, Empty>
typealias HStack2<A, B> = HCons<A, HStack1<B>>
typealias HStack3<A, B, C> = HCons<A, HStack2<B, C>>
typealias HStack4<A, B, C, D> = HCons<A, HStack3<B, C, D>>
typealias HStack5<A, B, C, D, E> = HCons<A, HStack4<B, C, D, E>>
typealias HStack6<A, B, C, D, E, F> = HCons<A, HStack5<B, C, D, E, F>>

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack1Component1")
operator fun <A> HStack1<A>.component1(): A = top


/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack2Component1")
operator fun <A, B> HStack2<A, B>.component1(): B = rest.component1()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack2Component2")
operator fun <A, B> HStack2<A, B>.component2(): A = top


/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack3Component1")
operator fun <A, B, C> HStack3<A, B, C>.component1(): C = rest.component1()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack3Component2")
operator fun <A, B, C> HStack3<A, B, C>.component2(): B = rest.component2()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack3Component3")
operator fun <A, B, C> HStack3<A, B, C>.component3(): A = top


/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack4Component1")
operator fun <A, B, C, D> HStack4<A, B, C, D>.component1(): D = rest.component1()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack4Component2")
operator fun <A, B, C, D> HStack4<A, B, C, D>.component2(): C = rest.component2()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack4Component3")
operator fun <A, B, C, D> HStack4<A, B, C, D>.component3(): B = rest.component3()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack4Component4")
operator fun <A, B, C, D> HStack4<A, B, C, D>.component4(): A = top


/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack5Component1")
operator fun <A, B, C, D, E> HStack5<A, B, C, D, E>.component1(): E = rest.component1()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack5Component2")
operator fun <A, B, C, D, E> HStack5<A, B, C, D, E>.component2(): D = rest.component2()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack5Component3")
operator fun <A, B, C, D, E> HStack5<A, B, C, D, E>.component3(): C = rest.component3()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack5Component4")
operator fun <A, B, C, D, E> HStack5<A, B, C, D, E>.component4(): B = rest.component4()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack5Component5")
operator fun <A, B, C, D, E> HStack5<A, B, C, D, E>.component5(): A = top


/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack6Component1")
operator fun <A, B, C, D, E, F> HStack6<A, B, C, D, E, F>.component1(): F = rest.component1()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack6Component2")
operator fun <A, B, C, D, E, F> HStack6<A, B, C, D, E, F>.component2(): E = rest.component2()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack6Component3")
operator fun <A, B, C, D, E, F> HStack6<A, B, C, D, E, F>.component3(): D = rest.component3()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack6Component4")
operator fun <A, B, C, D, E, F> HStack6<A, B, C, D, E, F>.component4(): C = rest.component4()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack6Component5")
operator fun <A, B, C, D, E, F> HStack6<A, B, C, D, E, F>.component5(): B = rest.component5()

/**
 * Deconstruction operators extract values in the order they were pushed, effectively reversing the stack.
 */
@JvmName("tStack6Component6")
operator fun <A, B, C, D, E, F> HStack6<A, B, C, D, E, F>.component6(): A = top


/**
 * Turn a function that takes parameters on the stack into one that takes them in an HStack
 */
fun <Result> (() -> Result).forHStacks(): (Empty) -> Result =
    { _ -> this() }

/**
 * Turn a function that takes parameters on the stack into one that takes them in an HStack
 */
fun <A, Result> ((A) -> Result).forHStacks(): (HStack1<A>) -> Result =
    { (a) -> this(a) }

/**
 * Turn a function that takes parameters on the stack into one that takes them in an HStack
 */
fun <A, B, Result> ((A, B) -> Result).forHStacks(): (HStack2<B, A>) -> Result =
    { (a, b) -> this(a, b) }

/**
 * Turn a function that takes parameters on the stack into one that takes them in an HStack
 */
fun <A, B, C, Result> ((A, B, C) -> Result).forHStacks(): (HStack3<C, B, A>) -> Result =
    { (a, b, c) -> this(a, b, c) }

/**
 * Turn a function that takes parameters on the stack into one that takes them in an HStack
 */
fun <A, B, C, D, Result> ((A, B, C, D) -> Result).forHStacks(): (HStack4<D, C, B, A>) -> Result =
    { (a, b, c, d) -> this(a, b, c, d) }

/**
 * Turn a function that takes parameters on the stack into one that takes them in an HStack
 */
fun <A, B, C, D, E, Result> ((A, B, C, D, E) -> Result).forHStacks(): (HStack5<E, D, C, B, A>) -> Result =
    { (a, b, c, d, e) -> this(a, b, c, d, e) }

/**
 * Turn a function that takes parameters on the stack into one that takes them in an HStack
 */
fun <A, B, C, D, E, F, Result> ((A, B, C, D, E, F) -> Result).forHStacks(): (HStack6<F, E, D, C, B, A>) -> Result =
    { (a, b, c, d, e, f) -> this(a, b, c, d, e, f) }
