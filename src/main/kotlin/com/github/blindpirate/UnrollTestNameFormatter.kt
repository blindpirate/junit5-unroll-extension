package com.github.blindpirate

class UnrollTestNameFormatter(private val template: String) {
    fun format(invocationIndex: Int, arguments: Array<out Any>): String {
        val replaceResult = arguments.foldIndexed(template, this::formatIndex)
        return if (replaceResult == template) {
            "$template [$invocationIndex]"
        } else {
            replaceResult
        }
    }

    private fun formatIndex(index: Int, current: String, currentValue: Any): String {
        return current.replace("{$index}", currentValue.toString())
    }
}
