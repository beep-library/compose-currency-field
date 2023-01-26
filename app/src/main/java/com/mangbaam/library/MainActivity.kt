package com.mangbaam.library

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mangbaam.library.ui.theme.CurrencyFieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyFieldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    companion object {
        const val TAG = "로그"
    }
}

@Composable
fun MainScreen() {
    var displayed by remember {
        mutableStateOf("")
    }
    var amount by remember {
        mutableStateOf("100")
    }
    Column {
        InfoText(text = "표시된 값: $displayed")
        InfoText(text = "금액: $amount")
        CurrencyField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.LightGray)
                .padding(30.dp),
            initAmount = 1000,
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
            onTextChanged = {
                Log.d(MainActivity.TAG, "onTextChanged: $it")
                displayed = it
            }, onValueChanged = {
                Log.d(MainActivity.TAG, "onValueChanged: $it")
                amount = it.toString()
            },
            rearSymbol = true,
            maxValue = null,
            maxLength = null
        )
    }
}

@Composable
fun InfoText(text: String) {
    Text(
        text = text, style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CurrencyFieldTheme {
        MainScreen()
    }
}