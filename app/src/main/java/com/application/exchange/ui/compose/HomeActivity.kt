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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    var amount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }
    val showDialog = remember { mutableStateOf(false) }
    viewModel.fetchExchangeRateFlow()

    val openDialog: () -> Unit = {
        showDialog.value = true
    }

    if (showDialog.value) ShowCurrencies(showDialog, selectedCurrency, viewModel)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = modifier.fillMaxWidth(),
                value = amount,
                onValueChange = { amount = it },
                label = {
                    Text(
                        stringResource(id = R.string.enter_the_amount_the_convert),
                        color = Color.Black
                    )
                },
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = selectedCurrency,
                onValueChange = { amount = it },
                Modifier.clickable {
                    amount = "Clicked"
                    openDialog.invoke()
                },
                label = {
                    Text(
                        stringResource(id = R.string.select_currency),
                        color = Color.Black
                    )
                },
                enabled = false
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    viewModel.fetchExchangeRateFlow()
                }, enabled = amount.isNotEmpty()
            ) {
                Text(text = "Convert")
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowCurrencies(
    showDialog: MutableState<Boolean>,
    selectedCurrency: String,
    viewModel: HomeViewModel
) {
    val list by viewModel.exchangeCurrency.observeAs
    BasicAlertDialog(
        onDismissRequest = { showDialog.value = false },

        ) {
        Surface(
            modifier = Modifier
                .height(600.dp)
                .fillMaxWidth()
        ) {
            LazyColumn {
                list.value.f

            }
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