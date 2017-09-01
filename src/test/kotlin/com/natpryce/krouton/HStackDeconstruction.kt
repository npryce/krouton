package com.natpryce.krouton

import org.junit.Test

class HStackDeconstruction {
    @Test
    fun `deconstructs_typed_stacks_back_into_order`() {
        val x: HStack4<Int, String, Int, Int> = Empty + 1 + 2 + "three" + 4
        val (a, b, c, d) = x
        assert(a == 1)
        assert(b == 2)
        assert(c == "three")
        assert(d == 4)
    }
}