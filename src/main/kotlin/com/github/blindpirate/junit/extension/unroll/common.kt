package com.github.blindpirate.junit.extension.unroll

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.AnnotationUtils.findAnnotation
import java.lang.reflect.Method

const val BUG_WARNING = "BUG! Please report to maintainer via https://github.com/blindpirate/junit5-unroll-extension/issues"

internal fun lastParameterIsParam(context: ExtensionContext): Boolean = context.testMethod.map { it.parameterTypes.last().kotlin == Param::class }.orElse(false)

internal fun determineTestNameTemplate(context: ExtensionContext): String {
    val testMethod = context.testMethod.get()
    val unroll = findAnnotation(testMethod, Unroll::class.java)
    return if (unroll.isPresent && unroll.get().name != "") {
        unroll.get().name
    } else {
        testMethod.name
    }
}

private fun unexpectedResult(): IllegalArgumentException = IllegalArgumentException(BUG_WARNING)

internal fun getTestClassName(context: ExtensionContext): String = context.testClass.map(Class<*>::getName).orElseThrow(::unexpectedResult)

internal fun getTestMethodName(context: ExtensionContext): String = context.testMethod.map(Method::getName).orElseThrow(::unexpectedResult)

internal fun getTestMethodParameterCount(context: ExtensionContext): Int = context.testMethod.map(Method::getParameterCount).orElseThrow(::unexpectedResult)
