package com.github.blindpirate.junit.extension.unroll.unsafe

import com.github.blindpirate.junit.extension.unroll.Param
import io.github.glytching.junit.extension.system.SystemProperty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.stream.Collectors
import java.util.stream.Stream

class UnsafeFunctionsTest {
    @Test
    fun `can retrieve where function`() {
        val params = extractArguments("${javaClass.`package`.name}.TestClass", "testMethod")
        verifyParamData(params)
    }

    @ParameterizedTest
    @CsvSource("TestClass, anotherTestMethod", "TestClass, unknownMethod")
    fun `give good message when failing to find the where function`(className: String, methodName: String) {
        val exception = assertThrows<ParameterResolutionException> {
            extractArguments(className, methodName)
        }

        val prefix = "$className\$$methodName\$"

        assertEquals(exception.message, """
            Can't find param after searching 10 times. Tried:
            ${prefix}1
            ${prefix}2
            ${prefix}3
            ${prefix}4
            ${prefix}5
            ${prefix}6
            ${prefix}7
            ${prefix}8
            ${prefix}9
            ${prefix}10""".trimIndent())
    }

    @Test
    @SystemProperty(name = "max.anonymous.search.num", value = "11")
    fun `can change system property to control retry count`() {
        val params = extractArguments("${javaClass.`package`.name}.TestClass", "anotherTestMethod")
        verifyParamData(params)
    }
}

class `TestClass$testMethod$2` private constructor() : Function1<Param, Unit> {
    companion object {
        @JvmField
        val INSTANCE = `TestClass$testMethod$2`()
    }

    override fun invoke(p: Param) {
        p.populateData()
    }
}

class `TestClass$anotherTestMethod$11` private constructor() : Function1<Param, Unit> {
    companion object {
        @JvmField
        val INSTANCE = `TestClass$anotherTestMethod$11`()
    }

    override fun invoke(p: Param) {
        p.populateData()
    }
}

private fun Param.populateData() {
    (this as Any).`_`("1")
    this.`_`(2)
    this.`_`('3')
    this.`_`(kotlin.collections.listOf(4))
    this.`_`(kotlin.collections.mapOf(5 to 6))

    (this as Any).`_`("11")
    this.`_`(12)
    this.`_`(13L)
    this.`_`(kotlin.collections.listOf(14))
    this.`_`(kotlin.collections.mapOf(15 to 16))
}

private fun verifyParamData(arguments: Stream<out Array<Any>>) {
    val params = arguments.collect(Collectors.toList())
    assertTrue(params[0][0] is Param)
    assertTrue(params[0].sliceArray(1..5).contentEquals(arrayOf("1", 2, '3', listOf(4), mapOf(5 to 6))))
    assertTrue(params[1][0] is Param)
    assertTrue(params[1].sliceArray(1..5).contentEquals(arrayOf("11", 12, 13L, listOf(14), mapOf(15 to 16))))
}
