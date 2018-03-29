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
    throw ParameterResolutionException("Can't find param after searching ${determineRetryCount()} times. Tried:\n${createSearchedClassString(prefix)}")
}

private fun createSearchedClassString(prefix: String): String {
    return IntStream.range(1, determineRetryCount() + 1).mapToObj({ "$prefix$it" }).collect(Collectors.joining("\n"))
}

private fun determineRetryCount(): Int {
    return System.getProperty(MAX_SEARCH_SYSTEM_PROPERTY)?.toInt()
            ?: MAX_ANONYMOUS_SEARCH_NUM
}

private fun getParam(whereFunctionClass: Class<*>): Param {
    return Param().also { getFunctionInstance(whereFunctionClass).invoke(it) }.also { verifyArguments(it) }
}

private fun verifyArguments(param: Param) {
    if (param.isEmpty) {
        throw IllegalArgumentException("""Please at least specify three parameters in your method signature (including 'where'):
            |For example, we don't support this:
            |
            |@Unroll
            |fun testMethod(firstArg: Int, param:Param = where {
            |    1
            |    2
            |    3
            |}) {
            |    assert(firstArg > 0)
            |}
            |
            |In this case, you should declared a redundant parameter:
            |
            |@Unroll
            |fun testMethod(firstArg: Int, unusedArg: Int, param:Param = where {
            |    1 _ 0
            |    2 _ 0
            |    3 _ 0
            |}) {
            |    assert(firstArg > 0)
            |}
        """.trimMargin())
    }
}

@Suppress("UNCHECKED_CAST")
private fun getFunctionInstance(whereFunctionClass: Class<*>): Function1<Param, Unit> {
    val staticInstanceFiled = whereFunctionClass.getField("INSTANCE")
    staticInstanceFiled.isAccessible = true
    return staticInstanceFiled.get(null) as Function1<Param, Unit>
}

private fun isTargetClass(klass: Class<*>): Boolean {
    verifyNotInnerClass(klass)
    return klass.fields.any { isInstanceField(klass, it) } && implementsFunctionAToUnit(klass)
}

fun verifyNotInnerClass(klass: Class<*>) {
    if (implementsFunctionAToUnit(klass) && klass.fields.any { it.name == "this$0" }) {
        throw IllegalArgumentException("""Your where function references enclosing instance, which is not supported:
            |This is supported:
            |
            |@Unroll
            |fun testMethod(
            |   a:Int, b:Int, p:Param = {
            |       2 _ 1
            |   }
            |) {
            |   assert(a > b)
            |}
            |
            |This is not supported:
            |
            |@Unroll
            |fun testMethod(
            |   a:Int, b:Int, p:Param = {
            |       abs(-2) _ 1
            |   }
            |) {
            |   assert(a > b)
            |}
            |
            |fun abs(i:Int):Int = Math.abs(i)
        """.trimMargin())
    }
}

private fun isInstanceField(klass: Class<*>, field: Field): Boolean = field.name == "INSTANCE" && Modifier.isStatic(field.modifiers) && field.type == klass

private fun implementsFunctionAToUnit(klass: Class<*>): Boolean {
    return klass.genericInterfaces.isNotEmpty()
            && klass.genericInterfaces[0] is ParameterizedType
            && FUNCTION1.contentEquals((klass.genericInterfaces[0] as ParameterizedType).actualTypeArguments)
}
