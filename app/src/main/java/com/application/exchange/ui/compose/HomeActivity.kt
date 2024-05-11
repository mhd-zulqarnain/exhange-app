package com.application.exchange.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.exchange.R

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExchangeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, viewModel: HomeViewModel = viewModel()) {
    var text by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }
    val showDialog = remember { mutableStateOf(false) }

    val openDialog: () -> Unit = {
        showDialog.value = true
    }

    if (showDialog.value) ShowCurrencies(showDialog, selectedCurrency)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = {
                    Text(
                        stringResource(id = R.string.enter_the_amount_the_convert),
                        color = Color.Black
                    )
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    viewModel.fetchExchangeRateFlow()
                }) {
                Text(text = "Convert")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            ClickableText(
                text = AnnotatedString("text"),
                onClick = {
                    openDialog.invoke()
                    text = "Disabled"
                })
        }

    }

}

@Composable
fun ShowCurrencies(showDialog: MutableState<Boolean>, selectedCurrency: String) {
    Dialog(
        onDismissRequest = { showDialog.value = false },
    ) {
        Surface() {
            Text(text = "its a text view")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExchangeTheme {
        Greeting()
    }
}