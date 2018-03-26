package com.github.blindpirate.unsafe

import com.github.blindpirate.Param
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.stream.Stream

internal fun extractArguments(context: ExtensionContext): Stream<out Array<Any>> = determineParams(context).toStream()

private fun determineParams(context: ExtensionContext): Param {
    val testClass = context.testClass.orElseThrow(::unexpectedResult)
    val testMethodName = context.testMethod.orElseThrow(::unexpectedResult).name

    (1..MAX_ANONYMOUS_NUM).forEach {
        try {
            val klass = Class.forName("${testClass.name}\$$testMethodName\$$it")
            if (isTargetClass(klass)) {
                return getParam(klass)
            }
        } catch (e: ClassNotFoundException) {
        }
    }
    throw ParameterResolutionException("Can't find closure after trying $MAX_ANONYMOUS_NUM times")
}

private fun getParam(whereFunctionClass: Class<*>): Param {
    return Param().also { getFunctionInstance(whereFunctionClass).invoke(it) }
}

private fun getFunctionInstance(whereFunctionClass: Class<*>): Function1<Param, Unit> {
    val staticInstanceFiled = whereFunctionClass.getField("INSTANCE")
    staticInstanceFiled.isAccessible = true
    return staticInstanceFiled.get(null) as Function1<Param, Unit>
}

private fun isTargetClass(klass: Class<*>): Boolean {
    return klass.fields.any { isInstanceField(klass, it) } && implementsFunctionAToUnit(klass)
}

private const val MAX_ANONYMOUS_NUM = 10

private fun unexpectedResult(): IllegalArgumentException {
    return IllegalArgumentException()
}

private fun isInstanceField(klass: Class<*>, field: Field): Boolean = field.name == "INSTANCE" && Modifier.isStatic(field.modifiers) && field.type == klass

private fun implementsFunctionAToUnit(klass: Class<*>): Boolean {
    return klass.genericInterfaces.isNotEmpty()
            && klass.genericInterfaces[0] is ParameterizedType
            && FUNCTION1.contentEquals((klass.genericInterfaces[0] as ParameterizedType).actualTypeArguments)
}

private val FUNCTION1: Array<Class<*>> = arrayOf(Param::class.java, Unit::class.java)