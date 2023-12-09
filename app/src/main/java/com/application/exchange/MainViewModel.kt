package com.application.exchange

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.core.Result
import com.application.core.exchange.ExchangeRepository
import com.application.core.data.model.CoroutinesDispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {

    fun getStoresFlow() = viewModelScope.launch(dispatcherProvider.io) {
        // can be launched in a separate asynchronous job
        exchangeRepository.getExchangeRate().onEach { result ->
            withContext(dispatcherProvider.main) {
                when (result) {
                    is Result.Success -> {
                        Log.e("HomeViewModel", "Success ${result.data.size}")
                    }

                    is Result.Error -> {
                        Log.e("HomeViewModel", "Error")
                    }

                    is Result.Loading -> {
                        Log.e("HomeViewModel", "Loading")

                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}

