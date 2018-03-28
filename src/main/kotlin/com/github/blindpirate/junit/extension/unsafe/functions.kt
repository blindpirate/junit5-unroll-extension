package com.github.blindpirate.junit.extension.unsafe

import com.github.blindpirate.junit.extension.Param
import org.junit.jupiter.api.extension.ParameterResolutionException
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

private const val MAX_SEARCH_SYSTEM_PROPERTY = "max.anonymous.search.num"

private const val MAX_ANONYMOUS_SEARCH_NUM = 10

private val FUNCTION1: Array<Class<*>> = arrayOf(Param::class.java, Unit::class.java)

internal fun extractArguments(testClassName: String, testMethodName: String): Stream<out Array<Any>> {
    val prefix = "$testClassName\$$testMethodName\$"
    (1..determineRetryCount()).forEach {
        try {
            val klass = Class.forName("$prefix$it")
            if (isTargetClass(klass)) {
                return getParam(klass).toStream()
            }
        } catch (e: ClassNotFoundException) {
        }
    }
    throw ParameterResolutionException("Can't find param after search ${MAX_ANONYMOUS_SEARCH_NUM} times. Tried:\n${createSearchedClassString(prefix)}")
}

private fun createSearchedClassString(prefix: String): String {
    return IntStream.range(1, determineRetryCount() + 1).mapToObj({ "$prefix$it" }).collect(Collectors.joining("\n"))
}

private fun determineRetryCount(): Int {
    return System.getProperty(MAX_SEARCH_SYSTEM_PROPERTY)?.toInt()
            ?: MAX_ANONYMOUS_SEARCH_NUM
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


private fun isInstanceField(klass: Class<*>, field: Field): Boolean = field.name == "INSTANCE" && Modifier.isStatic(field.modifiers) && field.type == klass

private fun implementsFunctionAToUnit(klass: Class<*>): Boolean {
    return klass.genericInterfaces.isNotEmpty()
            && klass.genericInterfaces[0] is ParameterizedType
            && FUNCTION1.contentEquals((klass.genericInterfaces[0] as ParameterizedType).actualTypeArguments)
}
