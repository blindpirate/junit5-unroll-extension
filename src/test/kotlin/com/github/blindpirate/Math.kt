package com.github.blindpirate

import java.lang.Math

class Math {
    @Unroll
    fun `max number of {0} and {1} is {2}`(
            a: Int, b: Int, c: Int, param: Param = where {
                1 _ 3 _ 3
                7 _ 4 _ 7
                0 _ 0 _ 0
            }) {
        assert(Math.max(a, b) == c)
    }

    @Unroll
    fun `{0} {1} {2} {3} {4}`(
            a: String, b: String, c: String, d: String, param: Param = where {
                "a" _ "b" _ "c" _ "d"
                "e" _ "f" _ "g" _ "h"
            }) {
        println("$a - $b - $c - $d")
    }
}