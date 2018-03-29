package com.github.blindpirate.junit.extension.resources

import com.github.blindpirate.junit.extension.Param
import com.github.blindpirate.junit.extension.Unroll
import com.github.blindpirate.junit.extension.where
import org.junit.jupiter.api.TestReporter

typealias UnitUnit = () -> Unit

class TestWithManyAnonymousClasses(testReporter: TestReporter) {
    companion object {
        @JvmField
        var reporter: TestReporter? = null
    }

    init {
        TestWithManyAnonymousClasses.reporter = testReporter
    }

    @Unroll
    fun test(arg0: UnitUnit = {},
             arg1: UnitUnit = {},
             arg2: UnitUnit = {},
             arg3: UnitUnit = {},
             arg4: UnitUnit = {},
             arg5: UnitUnit = {},
             arg6: UnitUnit = {},
             arg7: UnitUnit = {},
             arg8: UnitUnit = {},
             arg9: UnitUnit = {},
             arg10: UnitUnit = {},
             arg11: UnitUnit = {},
             param: Param = where {
                 ::f _ ::f _ ::f _ ::f _ ::f _ ::f _ ::f _ ::f _ ::f _ ::f _ ::f _ ::f
                 ::g _ ::g _ ::g _ ::g _ ::g _ ::g _ ::g _ ::g _ ::g _ ::g _ ::g _ ::g
             }) {
        arg0.invoke()
        arg1.invoke()
        arg2.invoke()
        arg3.invoke()
        arg4.invoke()
        arg5.invoke()
        arg6.invoke()
        arg7.invoke()
        arg8.invoke()
        arg9.invoke()
        arg10.invoke()
        arg11.invoke()
    }

}

private fun f() {
    TestWithManyAnonymousClasses.reporter!!.publishEntry("output", "Report1")
}

private fun g() {
    TestWithManyAnonymousClasses.reporter!!.publishEntry("output", "Report2")
}
