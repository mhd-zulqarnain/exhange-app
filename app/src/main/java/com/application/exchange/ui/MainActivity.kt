package com.application.exchange.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.application.core.Result
import com.application.core.data.local.ExchangeEntity
import com.application.core.worker.ACTION_CLEAR_LOCAL_CACHE
import com.application.exchange.R
import com.application.exchange.databinding.ActivityMainBinding
import com.application.exchange.ui.adapter.ExchangeAdapter
import com.application.exchange.ui.adapter.SpinnerAdapter
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var exchangeList: List<ExchangeEntity>? = null
    private var selectedCurrency: ExchangeEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        viewModel.setStateEvent(ExchangeStateEvent.FetchExchangeRate)

        viewModel.exchangeCurrency.observe(
            this, Observer(this::handleExchangeResult)
        )

        binding.layoutMain.amountEditText.apply {
            doAfterTextChanged {
                viewModel.setStateEvent(
                    ExchangeStateEvent.ValidateConversion(
                        this.text.toString(), selectedCurrency
                    )
                )
            }
        }

        binding.layoutMain.currencyEditText.setOnClickListener {
            showCurrencySelection()
        }

        binding.layoutMain.btnConvert.setOnClickListener {
            viewModel.setStateEvent(
                ExchangeStateEvent.ConvertAmount(
                    binding.layoutMain.amountEditText.text.toString().toDouble(), selectedCurrency?.rate?:0.0
                )
            )
        }

        //register Receiver to clear cache
        val intentRefreshTokenExpire = IntentFilter(ACTION_CLEAR_LOCAL_CACHE)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            onClearCacheReceiver, intentRefreshTokenExpire
        )
    }

    private fun handleExchangeResult(result: ExchangeResult?) {
        when (result) {
            is ExchangeResult.ExchangeRateResult -> {
                handleSymbolDropDown(result)
            }

            is ExchangeResult.ExchangeRequestResult -> {
                handleConvertedResult(result.result)
            }

            is ExchangeResult.ValidateConversion -> {
                binding.layoutMain.btnConvert.isEnabled = result.result
            }

            else -> {
                //not required
            }
        }
    }

    private fun handleSymbolDropDown(result: ExchangeResult.ExchangeRateResult) {
        when (result.result) {
            is Result.Success -> {
                binding.layoutMain.progressBar.visibility = View.GONE
                exchangeList = result.result.data
            }

            is Result.Error -> {
                binding.layoutMain.layoutMain.visibility = View.GONE
                binding.layoutError.layoutError.visibility = View.VISIBLE

            }

            Result.Loading -> {
                binding.layoutMain.progressBar.visibility = View.VISIBLE
            }
        }

    }

    private fun handleConvertedResult(list: List<ExchangeEntity>) {

        val adapter = ExchangeAdapter(list, this)
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.alignItems = AlignItems.CENTER
        binding.layoutMain.rvExchangeRates.layoutManager = layoutManager
        binding.layoutMain.rvExchangeRates.adapter = adapter

    }

    private fun showCurrencySelection() {
        exchangeList?.let {
            val rates = ArrayList(it)
            val view = LayoutInflater.from(this).inflate(R.layout.exchange_dialog_list, null)
            val listView = view.findViewById<ListView>(R.id.lisView)
            val reasonsAdapter = SpinnerAdapter(this, rates)
            listView.adapter = reasonsAdapter
            val dialog: AlertDialog =
                MaterialAlertDialogBuilder(this).setTitle("Select Currency").setView(view)
                    .setCancelable(true).create()
            dialog.show()

            listView.setOnItemClickListener { _, _, position, _ ->
                selectedCurrency = rates[position]
                viewModel.setStateEvent(
                    ExchangeStateEvent.ValidateConversion(
                        binding.layoutMain.amountEditText.text.toString(), selectedCurrency
                    )
                )
                binding.layoutMain.currencyEditText.setText(rates[position].symbol)
                dialog.dismiss()
            }
        }
    }

    private val onClearCacheReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(
                    ACTION_CLEAR_LOCAL_CACHE, ignoreCase = true
                )
            ) {
                viewModel.clearCache()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).apply {
            unregisterReceiver(onClearCacheReceiver)
        }
    }
}
