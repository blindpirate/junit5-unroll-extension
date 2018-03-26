package com.github.blindpirate

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.AnnotationUtils.findAnnotation

internal fun lastParameterIsParam(context: ExtensionContext): Boolean = context.testMethod.get().parameterTypes.last().kotlin == Param::class

internal fun determineTestNameTemplate(context: ExtensionContext): String {
    val testMethod = context.testMethod.get()
    val unroll = findAnnotation(testMethod, Unroll::class.java)
    return if (unroll.isPresent && unroll.get().name != "") {
        unroll.get().name
    } else {
        testMethod.name
    }
}



