package com.github.blindpirate.junit.extension

import org.junit.jupiter.api.Test

class UnrollTestNameFormatterTest {
    @Test
    fun `can replace {number}`() {
        assertTemplateRender("{0}", "", "")
        assertTemplateRender("{0}{1}{2}", "[]{1}{2}", listOf<Any>())
        assertTemplateRender("{0}", "[foo, bar]", listOf("foo", "bar"))
        assertTemplateRender("{0}", "42", 42)
        assertTemplateRender("{0}", "foo", "foo")
        assertTemplateRender("{0} {1}{2}", "foo 42", "foo", 42, "")

        assertTemplateRender("{0} {1} {2}{3}{4}{{{}{}{}{}{5}{6}{7}{8}{9}{10}{11}",
                "0 1 234{{{}{}{}{}567891011", *(0..20).toList().toTypedArray())
    }

    @Test
    fun `fallback to methodName(index)`() {
        assertFallbackTemplateRender("method", "method [0]", 0)
        assertFallbackTemplateRender("method", "method [1]", 1)
        assertFallbackTemplateRender("method{}", "method{} [12345]", 12345)
    }

    private fun assertFallbackTemplateRender(template: String, expected: String, invocationIndex: Int) {
        assert(UnrollTestNameFormatter(template).format(invocationIndex, arrayOf("whatever")) == expected)
    }

    private fun assertTemplateRender(template: String, expected: String, vararg args: Any?) {
        assert(UnrollTestNameFormatter(template).format(0, args as Array<out Any>) == expected)
    }
}