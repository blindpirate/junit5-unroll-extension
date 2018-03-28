package com.github.blindpirate.junit.extension.fixtures

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier

class IntegrationTestExecutionListener : TestExecutionListener {
    val testCases: MutableMap<TestIdentifier, TestExecutionResult> = HashMap()
    val outputs: MutableList<String> = ArrayList()

    override fun executionFinished(identifier: TestIdentifier, result: TestExecutionResult) {
        testCases[identifier] = result
    }

    override fun reportingEntryPublished(identifier: TestIdentifier, entry: ReportEntry) {
        outputs.add(entry.keyValuePairs["output"]!!)
    }
}