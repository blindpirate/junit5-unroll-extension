package com.github.blindpirate.junit.extension

import com.github.blindpirate.junit.extension.fixtures.IntegrationTestExecutionListener
import com.github.blindpirate.junit.extension.testcases.TestWithLongParameterList
import com.github.blindpirate.junit.extension.testcases.TestWithMultipleTypes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory

class NormalCasesTest {

    @Test
    fun `can apply a very long parameter list`() {
        val listener = runTest(TestWithLongParameterList::class.java)
        assertContainsTests(listener, "a_b_c_e_f_g_h_i_j_k", "l_m_n_o_p_q_r_s_t_u")
        assertEquals(listener.outputs, listOf("a_b_c_e_f_g_h_i_j_k", "l_m_n_o_p_q_r_s_t_u"))
    }

    @Test
    fun `can specify multiple type`() {
        val listener = runTest(TestWithMultipleTypes::class.java)
        assertContainsTests(listener, "with multiple types [0]", "with multiple types [1]")
    }

    fun assertContainsTests(listener: IntegrationTestExecutionListener, vararg tests: String) {
        tests.all { test ->
            listener.testCases.any { entry ->
                entry.key.displayName == test && entry.value.status == SUCCESSFUL
            }
        }
    }

    fun runTest(testClass: Class<*>): IntegrationTestExecutionListener {
        val listener = IntegrationTestExecutionListener()
        val launcher = LauncherFactory.create()

        launcher.registerTestExecutionListeners(listener)
        launcher.execute(LauncherDiscoveryRequestBuilder.request().selectors(selectClass(testClass)).build())
        return listener
    }

}