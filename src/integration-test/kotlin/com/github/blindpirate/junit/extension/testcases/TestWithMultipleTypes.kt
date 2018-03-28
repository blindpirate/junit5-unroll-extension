package com.github.blindpirate.junit.extension.testcases

import com.github.blindpirate.junit.extension.Param
import com.github.blindpirate.junit.extension.Unroll
import com.github.blindpirate.junit.extension.where
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class TestWithMultipleTypes {
    @Unroll
    fun `with multiple types`(
            intArg: Int, stringArg: String, listArg: MutableList<*>, mapArg: MutableMap<*, *>, objectArg: Base, param: Param = where {
                1 _ "2" _ ArrayList<String>().also { it.add("3") } _ HashMap<String, String>().also { it["4"] = "5" } _ Sub1()
                1 _ "2" _ ArrayList<String>().also { it.add("3") } _ HashMap<String, String>().also { it["4"] = "5" } _ Sub2()
            }
    ) {
        assertEquals(intArg, 1)
        assertEquals(stringArg, "2")
        assertEquals(listArg[0], "3")
        assertEquals(mapArg["4"], "5")
        assertTrue(objectArg.javaClass in listOf(Sub1::class.java, Sub2::class.java))
    }

}

interface Base

class Sub1 : Base
class Sub2 : Base
