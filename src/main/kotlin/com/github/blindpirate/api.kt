package com.github.blindpirate

import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import java.util.stream.Stream

fun where(init: Param.() -> Unit): Param {
    return Param()
}

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@TestTemplate
@ExtendWith(UnrollExtension::class)
annotation class Unroll(val name: String = "")

class Param {
    private val arguments = ArrayList<ArrayList<Any>>()

    infix fun Any.`_`(value: Any): Param {
        arguments.add(mutableListOf(this, value) as ArrayList<Any>)
        return this@Param
    }

    infix fun Param.`_`(value: Any): Param {
        arguments.last().add(value)
        return this@Param
    }

    internal fun toStream(): Stream<out Array<Any>> {
        return arguments.stream().map(this::addThisToDataRow)
    }

    private fun addThisToDataRow(row: ArrayList<Any>): Array<Any> {
        return Array(row.size + 1, { index -> if (index >= row.size) this else row[index] })
    }
}