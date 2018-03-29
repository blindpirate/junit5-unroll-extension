package com.github.blindpirate.junit.extension.resources

import com.github.blindpirate.junit.extension.Param
import com.github.blindpirate.junit.extension.Unroll
import com.github.blindpirate.junit.extension.where
import org.gradle.internal.impldep.junit.framework.Assert.assertEquals
import org.gradle.internal.impldep.junit.framework.Assert.assertTrue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestReporter
import java.math.BigDecimal


class NormalTestCases(private val testReporter: TestReporter) {
    @Unroll
    fun `can have only 1 row of data`(
            a: String, b: Int, c: Long, param: Param = where {
                "1" _ 2 _ 3L
            }
    ) {
        assertEquals(a, "1")
        assertEquals(b, 2)
        assertEquals(c, 3L)
    }

    @Unroll
    fun `can have only 2 columns of data`(
            a: String, b: Number, param: Param = where {
                "aaa" _ 1L
                "bbb" _ 0.1
                "ccc" _ BigDecimal(1)
            }
    ) {
        assertEquals(a.length, 3)
        assertTrue(b.toDouble() > 0)
    }

    @Unroll
    fun `can have 1x2 data`(
            a: String, b: Number, param: Param = where {
                "aaa" _ 1L
            }
    ) {
        assertEquals(a.length, 3)
        assertTrue(b.toDouble() > 0)
    }

    @Unroll
    fun `can specify multiple type`(
            intArg: Int, stringArg: String, listArg: MutableList<*>, mapArg: MutableMap<*, *>, objectArg: Base, param: Param = where {
                1 _ "2" _ ArrayList<String>().also { it.add("3") } _ HashMap<String, String>().also { it["4"] = "5" } _ Sub1()
                1 _ "2" _ ArrayList<String>().also { it.add("3") } _ HashMap<String, String>().also { it["4"] = "5" } _ Sub2()
            }
    ) {
        Assertions.assertEquals(intArg, 1)
        Assertions.assertEquals(stringArg, "2")
        Assertions.assertEquals(listArg[0], "3")
        Assertions.assertEquals(mapArg["4"], "5")
        Assertions.assertTrue(objectArg.javaClass in listOf(Sub1::class.java, Sub2::class.java))
    }
}


interface Base

class Sub1 : Base
class Sub2 : Base