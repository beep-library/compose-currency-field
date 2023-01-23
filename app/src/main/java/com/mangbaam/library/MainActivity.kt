package com.mangbaam.library

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
                    CurrencyField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                        onTextChanged = {
                            Log.d(TAG, "onTextChanged: $it")
                        }, onValueChanged = {
                            Log.d(TAG, "onValueChanged: $it")
                        },
                        rearUnit = true,
                        unit = "☻"
                    )
                }
            }
        }
    }

    companion object {
        const val TAG = "로그"
    }
}
