package com.mangbaam.library

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mangbaam.library.ui.theme.CurrencyFieldTheme
import java.math.BigDecimal

/**
 * @param initAmount initial displayed amount. if this value is larger than [maxValue] or longer than [maxLength], [CurrencyField] will display "0"
 * @param maxValue max value. if null or by default, it have no limit
 * @param maxLength max length. if null or by default, it have no limit
 * @param onTextChanged callback of displayed text
 * @param onValueChanged callback of currency value
 * @param showUnit show currency unit or not
 * @param unit currency unit. It will obey current locale's currency unit for default
 * @param rearUnit display unit to end of currency if true else start of currency
 * @param textStyle text style for displayed text
 * @param editable controls the editable state of the [CurrencyField]. When false, the text field can not be modified, however, a user can focus it and copy text from it. Read-only text fields are usually used to display pre-filled forms that user can not edit
 * @param enabled controls the enabled state of the [CurrencyField]. When false, the text field will be neither editable nor focusable, the input of the text field will not be selectable
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s for this TextField. You can create and pass in your own remembered [MutableInteractionSource] if you want to observe [Interaction]s and customize the appearance / behavior of this TextField in different [Interaction]s.
 */
@Composable
fun CurrencyField(
    modifier: Modifier = Modifier,
    initAmount: BigDecimal = BigDecimal.ZERO,
    maxValue: BigDecimal? = null,
    maxLength: Int? = null,
    onTextChanged: (String) -> Unit = {},
    onValueChanged: (String) -> Unit = {},
    showUnit: Boolean = true,
    unit: String = "원",
    rearUnit: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    editable: Boolean = true,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val availInitAmount = if (
        maxValue != null && initAmount > maxValue ||
        maxLength != null && initAmount.toString().length > maxLength
    ) {
        "0"
    } else {
        initAmount.toString()
    }
    var amount by remember { mutableStateOf(availInitAmount) }

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
            maxValue?.let { max ->
                if (inputText > max) return@BasicTextField
            }
            amount = inputText.toString()
            onValueChanged(amount.toNumberStringOrDefault("0"))
            onTextChanged(visualText(amount, showUnit, unit, rearUnit))
        },
        visualTransformation = CurrencyVisualTransformation(unit, showUnit, rearUnit),
        modifier = Modifier
            .wrapContentSize()
            .then(modifier),
        readOnly = editable.not(),
        enabled = enabled,
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        interactionSource = interactionSource
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
    showUnit: Boolean,
    unit: String,
    rearUnit: Boolean,
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

@Preview(showBackground = true)
@Composable
fun CurrencyFieldPreview() {
    CurrencyFieldTheme {
        CurrencyField(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray)
                .padding(30.dp),
            initAmount = BigDecimal("300000"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
            onTextChanged = {
                Log.d(MainActivity.TAG, "onTextChanged: $it")
            }, onValueChanged = {
                Log.d(MainActivity.TAG, "onValueChanged: $it")
            },
            rearUnit = true,
            maxValue = BigDecimal("1000"),
            maxLength = 10
        )
    }
}