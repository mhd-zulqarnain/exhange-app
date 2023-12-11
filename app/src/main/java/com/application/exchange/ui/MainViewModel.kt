package com.application.exchange.ui

import android.util.Log
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
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {

    private val _exchangeCurrency = MutableLiveData<ExchangeResult>()

    private var cacheExchangeList: List<ExchangeEntity>? = null

    private val _loginButtonState = MutableLiveData<Boolean>()

    val loginButtonState: LiveData<Boolean> = _loginButtonState


    val exchangeCurrency: LiveData<ExchangeResult>
        get() = _exchangeCurrency

    fun setStateEvent(event: ExchangeStateEvent) {
        when (event) {
            ExchangeStateEvent.FetchExchangeRate -> {
                fetchExchangeRateFlow()
            }

            is ExchangeStateEvent.ConvertAmount -> {
                exchangeAmount(event.amount)
            }
        }

    }

    fun loginButtonValidation(amount: String, selectedCurrency: ExchangeEntity?) {
        _loginButtonState.value = amount.isNotEmpty() && selectedCurrency!=null
    }

    private fun fetchExchangeRateFlow() = viewModelScope.launch(dispatcherProvider.io) {
        // can be launched in a separate asynchronous job
        _exchangeCurrency.postValue(ExchangeResult.ExchangeRateResult(Result.Loading))
        exchangeRepository.getExchangeRate().onEach { result ->
            withContext(dispatcherProvider.main) {
                when (result) {
                    is Result.Success -> {
                        Log.e("HomeViewModel", "Success ${result.data.size}")
                        cacheExchangeList = result.data
                        _exchangeCurrency.postValue(ExchangeResult.ExchangeRateResult(result))
                    }

                    else -> {
                        _exchangeCurrency.postValue(ExchangeResult.ExchangeRateResult(result))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun exchangeAmount(amount: Double) {
        cacheExchangeList?.let {
            mapExchangedAmount(amount, it)
        }
    }

    private fun mapExchangedAmount(amount: Double, list: List<ExchangeEntity>) {
        list.forEach {
            it.convertedAmount = it.getConvertedAmount(amount)
        }
        _exchangeCurrency.postValue(ExchangeResult.ExchangeRequestResult(list))
    }

    fun clearCache() {
        cacheExchangeList=null
        exchangeRepository.clearCache()
        Log.e("ExchangeWorker","ExchangeWorker:clearCache")
        fetchExchangeRateFlow()

    }
}

sealed class ExchangeResult {
    data class ExchangeRateResult(val result: Result<List<ExchangeEntity>>) : ExchangeResult()
    data class ExchangeRequestResult(val result: List<ExchangeEntity>) : ExchangeResult()
}

sealed class ExchangeStateEvent {
    object FetchExchangeRate : ExchangeStateEvent()
    class ConvertAmount(val amount: Double) : ExchangeStateEvent()
}
