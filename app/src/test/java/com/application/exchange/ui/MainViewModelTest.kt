package com.application.exchange.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.application.core.Result
import com.application.core.data.local.ExchangeEntity
import com.application.core.exchange.ExchangeRepository
import com.application.exchange.getOrAwaitValue
import com.application.exchange.provideFakeCoroutinesDispatcherProvider
import com.application.exchange.setField
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

    private val list = listOf(ExchangeEntity(1, "ALL", 94.537512), ExchangeEntity(1, "AED", 3.6724))

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

    @Test
    fun `test ValidateConversion Success `() = runBlocking {

       val entity=  ExchangeEntity(1, "ALL", 94.537512)

        //given that user select currency and enter amount
        viewModel.setStateEvent(
            ExchangeStateEvent.ValidateConversion("123",entity)
        )

        val actual = viewModel.exchangeCurrency.getOrAwaitValue()
        val expected = ExchangeResult.ValidateConversion(true)

        // verify
        assertEquals(actual, expected)
    }

    @Test
    fun `test ValidateConversion when validation fails when user did not select currency and enter amount `() = runBlocking {

        //given that user did not select currency and enter amount
        viewModel.setStateEvent(
            ExchangeStateEvent.ValidateConversion("123",null)
        )

        val actual = viewModel.exchangeCurrency.getOrAwaitValue()
        val expected = ExchangeResult.ValidateConversion(false)

        // verify
        assertEquals(actual, expected)
    }
    @Test
    fun `test ValidateConversion when validation fails when user selects currency but did not enter the amount `() = runBlocking {
        val entity=  ExchangeEntity(1, "ALL", 94.537512)

        //given that user did not select currency and enter amounts
        viewModel.setStateEvent(
            ExchangeStateEvent.ValidateConversion("",entity)
        )

        val actual = viewModel.exchangeCurrency.getOrAwaitValue()
        val expected = ExchangeResult.ValidateConversion(false)

        // verify
        assertEquals(actual, expected)
    }

    @Test
    fun `test conversion rate logic`() = runBlocking {
        //given that the list is in cache
        setField(
            viewModel,
            viewModel.javaClass.getDeclaredField("cacheExchangeList"),
            list
        )

        viewModel.setStateEvent(
            ExchangeStateEvent.ConvertAmount(2.0, 3.0)
        )

        val actual = viewModel.exchangeCurrency.getOrAwaitValue()
        val expected = ExchangeResult.ExchangeRequestResult(list)

        val expectedConvertedRate =63.02500800000001
        val actualConvertedRate: Double = list.first().convertedAmount

        // verify converted rate are equal
        assertEquals(actualConvertedRate, expectedConvertedRate,0.0)
        assertEquals(actual, expected)
    }

}