package com.github.blindpirate.junit.extension.unroll.resources

import com.github.blindpirate.junit.extension.unroll.Param
import com.github.blindpirate.junit.extension.unroll.Unroll
import com.github.blindpirate.junit.extension.unroll.where

@Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")
class AbnormalTestCases {
    @Unroll
    fun `throws exception when parameter number doesn't match`(
            i: Int, s: String, param: Param = where {
                1 _ "2" _ 3
                4 _ "5" _ 6
            }) {
    }

    @Unroll
    fun `throws exception when parameter type doesn't match`(
            i: Int, s: String, param: Param = where {
                2L _ ""
                1 _ ""
            }
    ) {
    }

    @Unroll
    fun `throws exception when only 1 column of data`(
            arg: Int, param: Param = where {
                1
                2
                3
            }) {
    }

    @Unroll
    fun `throws exception when where function references outer instance`(
            a: Int, b: Int, param: Param = where {
                abs(-1) _ 1
            }) {
    }

    private fun abs(i: Int): Int = Math.abs(i)
}