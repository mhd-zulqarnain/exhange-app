package com.application.exchange.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.core.Result
import com.application.core.data.local.ExchangeEntity
import com.application.core.data.local.ExchangeModel
import com.application.exchange.R
import com.application.exchange.ui.ExchangeResult
import com.application.exchange.ui.ExchangeStateEvent

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
    val exchangeModel = ExchangeModel("USD", 0.0, 0.0)
    val exchange = remember { mutableStateOf(exchangeModel) }

    val showDialog = remember { mutableStateOf(false) }
    viewModel.fetchExchangeRateFlow()
    val result by viewModel.exchangeCurrency.observeAsState()
    val list: ArrayList<ExchangeEntity> = arrayListOf()
    val exchangeResultList  by remember { mutableStateOf(arrayListOf<ExchangeEntity>())}

    result?.let { result ->
        when (result) {
            is ExchangeResult.ExchangeRateResult -> when (result.result) {
                is Result.Success -> {
                    list.addAll(result.result.data)
                }

                else -> {
                    //not required
                }
            }

            is ExchangeResult.ExchangeRequestResult -> {
                exchangeResultList.addAll(result.result)
            }

            else -> {
                //not required
            }
        }
    }


    val openDialog: () -> Unit = {
        showDialog.value = true
    }

    if (showDialog.value) ShowCurrencies(showDialog, exchange, list)

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
                value = exchange.value.amount.toString(),
                onValueChange = {
                    exchange.value.amount = it.toDouble()
                },
                label = {
                    Text(
                        stringResource(id = R.string.enter_the_amount_the_convert),
                        color = Color.Black
                    )
                },
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(0.dp,10.dp)
        ) {
            OutlinedTextField(
                value = exchange.value.symbol,
                onValueChange = { exchange.value.symbol = it },
                Modifier.clickable {
                    openDialog.invoke()
                    list.clear()
                },
                label = {
                    Text(
                        stringResource(id = R.string.select_currency),
                        color = Color.Black
                    )
                },
                enabled = false
            )
            Button(

                onClick = {
                    viewModel.setStateEvent(
                        ExchangeStateEvent.ConvertAmount(
                            exchange.value.amount,
                            exchange.value.rate
                        )
                    )
                }, enabled = exchange.value.amount.toString().isNotEmpty()
            ) {
                Text(text = "Convert")
            }

        }

        ShowExchangeResult(exchangeResultList)

    }
}

@Composable
fun ShowExchangeResult(
    list: ArrayList<ExchangeEntity>,
) {
    LazyColumn(modifier = Modifier.padding(10.dp,0.dp) ) {
        items(list) { exchangeEntity ->
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "${exchangeEntity.symbol}:${exchangeEntity.rate.toString()}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowCurrencies(
    showDialog: MutableState<Boolean>,
    exchangeModel: MutableState<ExchangeModel>,
    list: ArrayList<ExchangeEntity>,
) {

    BasicAlertDialog(
        onDismissRequest = { showDialog.value = false },

        ) {
        Surface(
            modifier = Modifier
                .height(600.dp)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(list) { entity ->
                    Row {
                        ClickableText(
                            text = AnnotatedString(entity.symbol),
                            onClick = {
                                exchangeModel.value.symbol = entity.symbol
                                exchangeModel.value.rate = entity.rate
                                showDialog.value = false
                            },
                            modifier = Modifier.padding(10.dp),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 14.sp,
                            )
                        )
                    }
                }


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