package com.github.blindpirate.junit.extension

import com.github.blindpirate.junit.extension.unroll.fixtures.IntegrationTestExecutionListener
import com.github.blindpirate.junit.extension.unroll.fixtures.runTest
import com.github.blindpirate.junit.extension.unroll.resources.NormalTestCases
import com.github.blindpirate.junit.extension.unroll.resources.TestWithLongParameterList
import com.github.blindpirate.junit.extension.unroll.resources.TestWithManyAnonymousClasses
import io.github.glytching.junit.extension.system.SystemProperty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL

class NormalCasesTest {
    @Test
    fun `can have only 1 row of data`(testInfo: TestInfo) {
        val listener = runTest(NormalTestCases::class.java, getMethodName(testInfo))
        assertContainsTests(listener, "${getMethodName(testInfo)} [1]")
    }

    @Test
    fun `can have only 2 columns of data`(testInfo: TestInfo) {
        val listener = runTest(NormalTestCases::class.java, getMethodName(testInfo))
        assertContainsTests(listener, "${getMethodName(testInfo)} [1]", "${getMethodName(testInfo)} [2]", "${getMethodName(testInfo)} [3]")
    }

    @Test
    fun `can have 1x2 data`(testInfo: TestInfo) {
        val listener = runTest(NormalTestCases::class.java, getMethodName(testInfo))
        assertContainsTests(listener, "${getMethodName(testInfo)} [1]")
    }

    @Test
    fun `can apply a very long parameter list`() {
        val listener = runTest(TestWithLongParameterList::class.java)
        assertContainsTests(listener, "a_b_c_e_f_g_h_i_j_k", "l_m_n_o_p_q_r_s_t_u")
        assertEquals(listener.outputs, listOf("a_b_c_e_f_g_h_i_j_k", "l_m_n_o_p_q_r_s_t_u"))
    }

    @Test
    fun `can specify multiple type`(testInfo: TestInfo) {
        val listener = runTest(NormalTestCases::class.java, getMethodName(testInfo))
        assertContainsTests(listener, "${getMethodName(testInfo)} [1]", "${getMethodName(testInfo)} [2]")
    }

    @Test
    @SystemProperty(name = "max.anonymous.search.num", value = "13")
    fun `can use system property to control anonymous class searching`() {
        val listener = runTest(TestWithManyAnonymousClasses::class.java)
        assertContainsTests(listener, "test [1]", "test [2]")
        assertEquals(listener.outputs, MutableList(12, { "Report1" }).also { it.addAll(MutableList(12, { "Report2" })) })
    }

    private fun getMethodName(testInfo: TestInfo): String = testInfo.testMethod.get().name

    private fun assertContainsTests(listener: IntegrationTestExecutionListener, vararg tests: String) {
        assert(!listener.testCases.isEmpty())
        assert(tests.all { test ->
            listener.testCases.any { entry ->
                entry.key.displayName == test && entry.value.status == SUCCESSFUL && entry.key.isTest
            }
        })
    }
}