package com.mangbaam.library

import java.math.BigDecimal

/**
 * filtered string with `.` and digit. It could be empty string
 * @return number format string or empty string
 */
fun String.toNumberString(): String {
    return filter { c -> c.isDigit() || c == '.' }
}

fun String.toNumberStringOrDefault(defaultValue: String): String {
    return toNumberString().let {
        it.ifBlank { defaultValue }
    }
}

/**
 * make filtered string with `.` and digit to BigDecimal
 * @return BigDecimal. If filtered string is empty, returns `BigDecimal.ZERO`
 */
fun String.toBigDecimalOrZero(): BigDecimal {
    return toNumberString().toBigDecimalOrNull() ?: BigDecimal.ZERO
}

/**
 * divide string with [delimiter]
 * @param delimiter to divide
 * @param ignoreCase true to ignore character case when matching a character. By default `false`
 * @return divided strings Pair. if not contains [delimiter], returns `Pair(this, "")`
 */
fun String.divide(delimiter: Char, ignoreCase: Boolean = false): Pair<String, String> {
    val index = indexOf(delimiter, ignoreCase = ignoreCase)
    if (index == -1) {
        return Pair(this, "")
    }
    return Pair(substring(0 until index), substring(index + 1..lastIndex))
}

/**
 * Splits and join chunked string with [separator] each not exceeding the given [size].
The first string in the chunked strings may have fewer characters than the given [size].
 */
fun String.reverseChunked(size: Int, separator: Char = ','): String {
    val remain = length % size
    return if (remain > 0) {
        val left = substring(0 until remain)
        val right = substring(remain..lastIndex)
        val sb = StringBuilder(left)
        if (right.isNotBlank()) {
            sb.append(separator)
                .append(right.chunkedSequence(size).joinToString(separator.toString()))
        }
        sb.toString()
    } else {
        chunkedSequence(size).joinToString(separator.toString())
    }
}

/**
 * make string to currency format
 */
fun String.toFormattedNumberString(chunkSize: Int = 3): String {
    var (integer, decimal) = toNumberStringOrDefault("0").divide('.')
    decimal = decimal.dropLastWhile { c ->
        c == '0'
    }
    val sb = StringBuilder(integer.reverseChunked(chunkSize))
    if (decimal.isNotBlank()) {
        sb.append('.')
        sb.append(decimal)
    }
    return sb.toString()
}
