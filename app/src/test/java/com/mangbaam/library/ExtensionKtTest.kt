package com.mangbaam.library

import org.junit.Test
import java.math.BigDecimal

class ExtensionKtTest {

    @Test
    fun toNumberString() {
        assert("1a2b3c.4".toNumberString() == "123.4")
    }

    @Test
    fun toBigDecimalOrZero() {
        assert("9".repeat(100).toBigDecimalOrZero() == BigDecimal("9".repeat(100)))
    }

    @Test
    fun divide_withDot() {
        val (a, b) = "abc.de".divide('.')
        assert(a == "abc")
        assert(b == "de")
    }

    @Test
    fun divide_withoutDot() {
        val (a, b) = "abc".divide('.')
        assert(a == "abc")
        assert(b.isBlank())
    }

    @Test
    fun reverseChunked_12345() {
        assert("12345".reverseChunked(3) == "12,345")
    }

    @Test
    fun reverseChunked_123456() {
        assert("123456".reverseChunked(3) == "123,456")
    }

    @Test
    fun reverseChunked_0() {
        assert("0".reverseChunked(3) == "0")
    }

    @Test
    fun toFormattedNumberString() {
        assert("12345.67890".toFormattedNumberString(3) == "12,345.6789")
    }
}