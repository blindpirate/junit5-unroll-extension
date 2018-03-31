package com.github.blindpirate.junit.extension

import com.github.blindpirate.junit.extension.unroll.fixtures.IntegrationTestExecutionListener
import com.github.blindpirate.junit.extension.unroll.fixtures.runTest
import com.github.blindpirate.junit.extension.unroll.resources.AbnormalTestCases
import com.github.blindpirate.junit.extension.unroll.resources.TestWithManyAnonymousClasses
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestIdentifier

typealias TestAssertion = Function2<TestIdentifier, TestExecutionResult, Boolean>

class AbnormalCasesTest {
    @Test
    fun `throws exception when parameter number doesn't match`(testInfo: TestInfo) {
        val listener = runTest(AbnormalTestCases::class.java, testInfo.testMethod.get().name)
        assertFailedTest(listener, { _, result ->
            isIllegalArgumentExceptionContaining(result, "expected: 2\nactual: 3")
        })
    }

    @Test
    fun `throws exception when parameter type doesn't match`(testInfo: TestInfo) {
        val listener = runTest(AbnormalTestCases::class.java, testInfo.testMethod.get().name)
        assertFailedTest(listener, { _, result ->
            resultContainsMessage(result, "but a value assignment compatible with [int] is required.")
        })
    }

    @Test
    fun `throws exception when anonymous class can't be found`() {
        val listener = runTest(TestWithManyAnonymousClasses::class.java)
        assertFailedTest(listener, { identifier, result ->
            identifier.displayName.contains("test(") && resultContainsMessage(result, "Can't find param after searching")
        })
    }

    @Test
    fun `throws exception when where function references outer instance`(testInfo: TestInfo) {
        val listener = runTest(AbnormalTestCases::class.java, testInfo.testMethod.get().name)
        assertFailedTest(listener, { _, result ->
            isIllegalArgumentExceptionContaining(result, "Your where function references enclosing instance")
        })
    }

    @Test
    fun `throws exception when only 1 column of data`(testInfo: TestInfo) {
        val listener = runTest(AbnormalTestCases::class.java, testInfo.testMethod.get().name)
        assertFailedTest(listener, { _, result ->
            isIllegalArgumentExceptionContaining(result, "Please at least specify three parameters")
        })
    }

    private fun isIllegalArgumentExceptionContaining(result: TestExecutionResult, message: String): Boolean {
        return result.throwable.map { it is IllegalArgumentException }.orElse(false) && resultContainsMessage(result, message)
    }

    private fun resultContainsMessage(result: TestExecutionResult, message: String): Boolean {
        return result.throwable.map { it.message?.contains(message) ?: false }.orElse(false)
    }

    private fun assertFailedTest(listener: IntegrationTestExecutionListener, failedTestAssertion: TestAssertion) {
        assert(listener.testCases.any { entry ->
            failedTestAssertion.invoke(entry.key, entry.value) && entry.value.status == TestExecutionResult.Status.FAILED
        })
    }
}