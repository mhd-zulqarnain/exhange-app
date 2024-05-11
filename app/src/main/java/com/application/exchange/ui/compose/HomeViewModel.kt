package com.application.exchange.ui.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.core.Result
import com.application.core.data.local.ExchangeEntity
import com.application.core.data.model.CoroutinesDispatcherProvider
import com.application.core.exchange.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {
    private var cacheExchangeList: List<ExchangeEntity>? = null

    private val _exchangeCurrency = MutableLiveData<ExchangeResult>()
    val exchangeCurrency: LiveData<ExchangeResult>
        get() = _exchangeCurrency

    fun setStateEvent(event: ExchangeStateEvent) {
        when (event) {
            ExchangeStateEvent.FetchExchangeRate -> {
                fetchExchangeRateFlow()
            }

            is ExchangeStateEvent.ConvertAmount -> {
                exchangeAmount(event.amount,event.selectedCurrencyUSD)
            }

            is ExchangeStateEvent.ValidateConversion -> {
                convertButtonValidation(event.amount, event.selectedCurrency)
            }
        }

    }

    private fun convertButtonValidation(amount: String, selectedCurrency: ExchangeEntity?) {
        _exchangeCurrency.postValue(ExchangeResult.ValidateConversion(amount.isNotEmpty() && selectedCurrency != null))
    }

     fun fetchExchangeRateFlow() = viewModelScope.launch(dispatcherProvider.io) {
        // can be launched in a separate asynchronous job
        exchangeRepository.getExchangeRate().onEach { result ->
            withContext(dispatcherProvider.main) {
                when (result) {
                    is Result.Success -> {
                        cacheExchangeList = result.data
                        _exchangeCurrency.postValue(ExchangeResult.ExchangeRateResult(result))
                    }
                    is Result.Loading -> {
                        _exchangeCurrency.postValue(ExchangeResult.ExchangeRateResult(Result.Loading))
                    }

                    else -> {
                        _exchangeCurrency.postValue(ExchangeResult.ExchangeRateResult(result))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun exchangeAmount(amount: Double ,selectedCurrencyUSD:Double) {
        cacheExchangeList?.let { list ->
            list.forEach {
                it.convertedAmount = it.getConvertedAmount(amount,selectedCurrencyUSD)
            }
            _exchangeCurrency.postValue(ExchangeResult.ExchangeRequestResult(list))
        }
    }

    fun clearCache() {
        cacheExchangeList = null
        exchangeRepository.clearCache()
        fetchExchangeRateFlow()
    }
}

sealed class ExchangeResult {
    data class ExchangeRateResult(val result: Result<List<ExchangeEntity>>) : ExchangeResult()
    data class ExchangeRequestResult(val result: List<ExchangeEntity>) : ExchangeResult()
    data class ValidateConversion(val result: Boolean) : ExchangeResult()
}

sealed class ExchangeStateEvent {
    object FetchExchangeRate : ExchangeStateEvent()
    class ConvertAmount(val amount: Double,val selectedCurrencyUSD: Double) : ExchangeStateEvent()
    class ValidateConversion(val amount: String, val selectedCurrency: ExchangeEntity?) :
        ExchangeStateEvent()
}
