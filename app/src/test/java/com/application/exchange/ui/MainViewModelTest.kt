package com.application.exchange.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.application.core.Result
import com.application.core.data.local.ExchangeEntity
import com.application.core.exchange.ExchangeRepository
import com.application.exchange.getOrAwaitValue
import com.application.exchange.provideFakeCoroutinesDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


class MainViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate =
        newSingleThreadContext("UI thread") // https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/

    private val exchangeRepository: ExchangeRepository = mock()
    private val viewModel =
        MainViewModel(
            exchangeRepository,
            provideFakeCoroutinesDispatcherProvider(),
        )

    private val list = listOf(ExchangeEntity(1, "US", 12.2), ExchangeEntity(1, "AMD", 12.2))

    @Before
    fun before() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun `test FetchExchangeRate success `() = runBlocking {

        whenever(
            exchangeRepository.getExchangeRate(
            )
        ).thenReturn(
            flowOf(
                Result.Success(
                    list
                )
            )
        )

        viewModel.setStateEvent(
            ExchangeStateEvent.FetchExchangeRate
        )

        val actual = viewModel.exchangeCurrency.getOrAwaitValue()
        val expected = ExchangeResult.ExchangeRateResult(Result.Success(list))

        // verify
        assertEquals(actual, expected)
    }

    @Test
    fun `test FetchExchangeRate Failure `() = runBlocking {

        val exception = Exception("")
        whenever(
            exchangeRepository.getExchangeRate(
            )
        ).thenReturn(
            flowOf(
                Result.Error(
                    exception
                )
            )
        )

        viewModel.setStateEvent(
            ExchangeStateEvent.FetchExchangeRate
        )

        val actual = viewModel.exchangeCurrency.getOrAwaitValue()
        val expected = ExchangeResult.ExchangeRateResult(Result.Error(exception))

        // verify
        assertEquals(actual, expected)
    }

}