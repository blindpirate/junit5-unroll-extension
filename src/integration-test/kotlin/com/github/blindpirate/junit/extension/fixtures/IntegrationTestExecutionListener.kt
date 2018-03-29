package com.github.blindpirate.junit.extension.fixtures

import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory

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

fun runTest(testClass: Class<*>, testMethod: String = ""): IntegrationTestExecutionListener {
    val listener = IntegrationTestExecutionListener()
    val launcher = LauncherFactory.create()

    launcher.registerTestExecutionListeners(listener)
    launcher.execute(LauncherDiscoveryRequestBuilder.request()
            .selectors(determineSelectors(testClass,testMethod))
            .build())
    return listener
}

fun determineSelectors(testClass: Class<*>, testMethod: String): DiscoverySelector {
    return if (testMethod == "") {
        selectClass(testClass)
    } else {
        selectMethod(testClass, testClass.methods.first { it.name == testMethod })
    }
}
