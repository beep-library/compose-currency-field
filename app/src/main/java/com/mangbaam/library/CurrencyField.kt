package com.mangbaam.library

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * @param maxValue max value. if null or default, it have no limit. it would better be number format
 * @param onTextChanged callback of displayed text
 * @param onValueChanged callback of currency value
 * @param showUnit show currency unit or not
 * @param unit currency unit. It will obey current locale's currency unit for default
 * @param textStyle text style for displayed text
 * @param editable editable or not
 */
@Composable
fun CurrencyField(
    modifier: Modifier = Modifier,
    initAmount: String = "0",
    maxValue: String? = null,
    maxLength: Int? = null,
    onTextChanged: (String) -> Unit = {},
    onValueChanged: (String) -> Unit = {},
    showUnit: Boolean = true,
    unit: String = "원",
    rearUnit: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    editable: Boolean = true
) {
    var amount by remember { mutableStateOf(initAmount) }

    BasicTextField(
        value = amount,
        onValueChange = {
            if (amount.contains('.') && it.count { c -> c == '.' } > 1) return@BasicTextField
            val inputText = it.toBigDecimalOrZero()
            if (inputText.equals(0)) {
                return@BasicTextField
            }
            maxLength?.let { maxLength ->
                if (inputText.toString().length > maxLength) return@BasicTextField
            }
            maxValue?.let {
                val max = maxValue.toBigDecimalOrZero()
                if (inputText > max) return@BasicTextField
            }
            amount = inputText.toString()
            onValueChanged(amount.toNumberStringOrDefault("0"))
            onTextChanged(visualText(amount, showUnit, unit, rearUnit))
        },
        visualTransformation = CurrencyVisualTransformation(unit, showUnit, rearUnit),
        modifier = Modifier
            .wrapContentHeight()
            .then(modifier),
        readOnly = editable.not(),
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

class CurrencyVisualTransformation(
    private val unit: String,
    private val showUnit: Boolean,
    private val rearUnit: Boolean
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val currencyFormat = text.text.toFormattedNumberString(CHUNK_SIZE)
        return TransformedText(
            text = AnnotatedString(visualText(text.text, showUnit, unit, rearUnit)),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val rightLength = text.lastIndex - offset
                    val commasAtRight = rightLength / CHUNK_SIZE
                    val transformedIndex = currencyFormat.lastIndex - (rightLength + commasAtRight)

                    return transformedIndex.coerceIn(0..currencyFormat.length).let {
                        if (showUnit && rearUnit.not()) {
                            it + unit.length
                        } else {
                            it
                        }
                    }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val commas = (text.lastIndex / CHUNK_SIZE).coerceAtLeast(0)
                    val rightOffset = currencyFormat.lastIndex - offset
                    val commasAtRight = rightOffset / (CHUNK_SIZE + 1)

                    return if (showUnit && rearUnit.not()) {
                        (offset - (commas - commasAtRight) - unit.length)
                            .coerceIn(0..text.lastIndex + unit.length)
                    } else {
                        (offset - (commas - commasAtRight)).coerceIn(0..text.length)
                    }
                }
            }
        )
    }

    companion object {
        const val CHUNK_SIZE = 3
    }
}

fun visualText(
    amount: String,
    showUnit: Boolean = true,
    unit: String = "원",
    rearUnit: Boolean = true,
): String {
    val sb = StringBuilder()
    if (showUnit && rearUnit.not()) {
        sb.append(unit)
    }
    sb.append(
        amount.toFormattedNumberString()
    )
    if (showUnit && rearUnit) {
        sb.append(unit)
    }
    return sb.toString()
}
