package com.github.blindpirate.junit.extension.unroll.resources

import com.github.blindpirate.junit.extension.unroll.Param
import com.github.blindpirate.junit.extension.unroll.Unroll
import com.github.blindpirate.junit.extension.unroll.where
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestReporter

@Suppress("UNUSED_PARAMETER")
class TestWithLongParameterList(private val testReporter: TestReporter) {
    @Unroll
    @DisplayName("can apply a very long parameter list")
    fun `{0}_{1}_{2}_{3}_{4}_{5}_{6}_{7}_{8}_{9}`(
            arg0: String, arg1: String, arg2: String, arg3: String, arg4: String,
            arg5: String, arg6: String, arg7: String, arg8: String, arg9: String, param: Param = where {
                "a" _ "b" _ "c" _ "e" _ "f" _ "g" _ "h" _ "i" _ "j" _ "k"
                "l" _ "m" _ "n" _ "o" _ "p" _ "q" _ "r" _ "s" _ "t" _ "u"
            }) {
        testReporter.publishEntry("output", "${arg0}_${arg1}_${arg2}_${arg3}_${arg4}_${arg5}_${arg6}_${arg7}_${arg8}_${arg9}")
    }
}