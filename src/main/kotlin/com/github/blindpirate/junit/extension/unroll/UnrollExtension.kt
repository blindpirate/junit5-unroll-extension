package com.github.blindpirate.junit.extension.unroll

import com.github.blindpirate.junit.extension.unroll.unsafe.extractArguments
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import java.util.stream.Stream

class UnrollExtension : TestTemplateInvocationContextProvider {
    override fun supportsTestTemplate(context: ExtensionContext?): Boolean = lastParameterIsParam(context!!)

    override fun provideTestTemplateInvocationContexts(context: ExtensionContext?): Stream<TestTemplateInvocationContext> {
        return extractArguments(getTestClassName(context!!), getTestMethodName(context)).map { arguments ->
            verifyArgumentNumberMatch(arguments!!, context)
            UnrollTestTemplateInvocationContext(arguments, determineTestNameTemplate(context))
        }
    }

    private fun verifyArgumentNumberMatch(arguments: Array<Any>, context: ExtensionContext) {
        val expectedCount = getTestMethodParameterCount(context)
        if (arguments.size != expectedCount) {
            throw IllegalArgumentException("""Wow, seems your arguments doesn't match the parameters declared in: ${getTestMethodName(context)}()
                |expected: ${expectedCount - 1}
                |actual: ${arguments.size - 1}
            """.trimMargin())
        }
    }
}

class UnrollTestParameterResolver(private val arguments: Array<out Any>) : ParameterResolver {
    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        return lastParameterIsParam(extensionContext!!)
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Any {
        return arguments[parameterContext?.index!!]
    }
}

class UnrollTestTemplateInvocationContext(private val arguments: Array<out Any>, testNameTemplate: String) : TestTemplateInvocationContext {
    private val nameFormatter = UnrollTestNameFormatter(testNameTemplate)
    override fun getDisplayName(invocationIndex: Int): String = nameFormatter.format(invocationIndex, arguments)
    override fun getAdditionalExtensions(): MutableList<Extension> = mutableListOf(UnrollTestParameterResolver(arguments))
}
