package com.github.blindpirate

import java.lang.Math

class Math {
    @Unroll
    fun `max number of #1 and #2 is #3`(
            a: Int, b: Int, c: Int, param: Param = where {
                1 _ 3 _ 3
                7 _ 4 _ 7
                0 _ 0 _ 0
            }) {
        assert(Math.max(a, b) == c)
    }
}