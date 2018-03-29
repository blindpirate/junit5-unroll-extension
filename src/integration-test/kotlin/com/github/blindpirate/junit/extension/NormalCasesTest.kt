package com.github.blindpirate.junit.extension

import com.github.blindpirate.junit.extension.fixtures.IntegrationTestExecutionListener
import com.github.blindpirate.junit.extension.fixtures.runTest
import com.github.blindpirate.junit.extension.resources.NormalTestCases
import com.github.blindpirate.junit.extension.resources.TestWithLongParameterList
import com.github.blindpirate.junit.extension.resources.TestWithManyAnonymousClasses
import io.github.glytching.junit.extension.system.SystemProperty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL

class NormalCasesTest {
    @Test
    fun `can have only 1 row of data`(testInfo: TestInfo) {
        val listener = runTest(NormalTestCases::class.java, testInfo.testMethod.get().name)
        assertContainsTests(listener, "${testInfo.displayName} [0]")
    }

    @Test
    fun `can have only 2 columns of data`(testInfo: TestInfo) {
        val listener = runTest(NormalTestCases::class.java, testInfo.testMethod.get().name)
        assertContainsTests(listener, "${testInfo.displayName} [0]", "${testInfo.displayName} [1]", "${testInfo.displayName} [2]")
    }

    @Test
    fun `can have 1x2 data`(testInfo: TestInfo) {
        val listener = runTest(NormalTestCases::class.java, testInfo.testMethod.get().name)
        assertContainsTests(listener, "${testInfo.displayName} [0]")
    }

    @Test
    fun `can apply a very long parameter list`() {
        val listener = runTest(TestWithLongParameterList::class.java)
        assertContainsTests(listener, "a_b_c_e_f_g_h_i_j_k", "l_m_n_o_p_q_r_s_t_u")
        assertEquals(listener.outputs, listOf("a_b_c_e_f_g_h_i_j_k", "l_m_n_o_p_q_r_s_t_u"))
    }

    @Test
    fun `can specify multiple type`(testInfo: TestInfo) {
        val listener = runTest(NormalTestCases::class.java, testInfo.testMethod.get().name)
        assertContainsTests(listener, "${testInfo.displayName} [0]", "${testInfo.displayName} [1]")
    }

    @Test
    @SystemProperty(name = "max.anonymous.search.num", value = "13")
    fun `can use system property to control anonymous class searching`() {
        val listener = runTest(TestWithManyAnonymousClasses::class.java)
        assertContainsTests(listener, "test [0]", "test [1]")
        assertEquals(listener.outputs, MutableList(12, { "Report1" }).also { it.addAll(MutableList(12, { "Report2" })) })
    }

    private fun assertContainsTests(listener: IntegrationTestExecutionListener, vararg tests: String) {
        assert(!listener.testCases.isEmpty())
        tests.all { test ->
            listener.testCases.any { entry ->
                entry.key.displayName == test && entry.value.status == SUCCESSFUL
            }
        }
    }
}