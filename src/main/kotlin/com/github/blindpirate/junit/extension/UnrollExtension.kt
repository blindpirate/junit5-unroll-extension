package com.github.blindpirate.junit.extension

import com.github.blindpirate.junit.extension.unsafe.extractArguments
import org.junit.jupiter.api.extension.*
import java.util.stream.Stream

class UnrollExtension : TestTemplateInvocationContextProvider {
    override fun supportsTestTemplate(context: ExtensionContext?): Boolean = lastParameterIsParam(context!!)

    override fun provideTestTemplateInvocationContexts(context: ExtensionContext?): Stream<TestTemplateInvocationContext> {
        return extractArguments(getTestClassName(context), getTestMethodName(context)).map { arguments ->
            UnrollTestTemplateInvocationContext(arguments, determineTestNameTemplate(context!!))
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
