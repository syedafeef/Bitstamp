package com.afeef.cryptoapi.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.afeef.cryptoapi.R
import com.afeef.cryptoapi.model.*
import com.afeef.cryptoapi.util.AppConstants
import com.afeef.cryptoapi.viewmodel.MainActivityViewModel
import com.google.android.material.textfield.TextInputLayout
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import com.afeef.cryptoapi.ui.adapter.OrderBookListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.afeef.cryptoapi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var autoCompleteTextView :AutoCompleteTextView
    private lateinit var textInputLayout : TextInputLayout
    private lateinit var rlTicker : RelativeLayout
    private lateinit var llOrderList : LinearLayout
    private lateinit var llSearchText : LinearLayout


    private lateinit var tvCurrencyPair : TextView
    private lateinit var tvDate : TextView
    private lateinit var tvOpen : TextView
    private lateinit var tvLow : TextView
    private lateinit var tvVolume : TextView
    private lateinit var tvHigh : TextView
    private lateinit var tvLast : TextView
    private lateinit var tv_view_hide_order_book : TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var orderAdapter: OrderBookListAdapter
    private lateinit var orderList: RecyclerView

    private var searchCryptoPair = ""

    private lateinit var toast:Toast
    private lateinit var context: Context
    private val viewModel: MainActivityViewModel by instance()

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        context = this

        initUi()

        setClickListeners()
        observeViewModel()
    }

    private fun initUi() {
        autoCompleteTextView = findViewById(R.id.et_searchbar)
        textInputLayout = findViewById(R.id.searchInputlayout)

        progressBar = findViewById(R.id.pb_api)
        rlTicker = findViewById(R.id.rl_ticker_detail)
        llOrderList = findViewById(R.id.ll_orderlist)
        llSearchText = findViewById(R.id.ll_searchtext)
        floatingActionButton = findViewById(R.id.fab_refresh)


        tvCurrencyPair = findViewById(R.id.tv_currency_pair_name)
        tvDate = findViewById(R.id.tv_date)
        tvOpen = findViewById(R.id.tv_open_value)
        tvLow = findViewById(R.id.tv_low_value)
        tvVolume = findViewById(R.id.tv_volume_value)
        tvHigh = findViewById(R.id.tv_high_value)
        tvLast = findViewById(R.id.tv_last_value)

        tv_view_hide_order_book = findViewById(R.id.tv_view_hide_order_book)
        orderList = findViewById(R.id.rv_orderList)

        val autoCompleteTvAdapter: ArrayAdapter<String> = ArrayAdapter<String>( context, android.R.layout.select_dialog_item, AppConstants.supportedCryptoValue)
        autoCompleteTextView.setAdapter(autoCompleteTvAdapter)


        orderAdapter = OrderBookListAdapter()
        orderList.adapter = orderAdapter
    }

    private fun setClickListeners() {
        autoCompleteTextView.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchOperation()
            }
            true
        }

        textInputLayout.setEndIconOnClickListener{
            searchOperation()
        }

        tv_view_hide_order_book.setOnClickListener {
            //hide and show UI

            if(llOrderList.isVisible){
                tv_view_hide_order_book.text = getString(R.string.view_order_book)
                makeVisibilityGone(llOrderList)
            }else{
                tv_view_hide_order_book.text = getString(R.string.hide_order_book)
                viewModel.getOrderBookData(searchCryptoPair)
            }

        }

        floatingActionButton.setOnClickListener {
            viewModel.getTickerData(searchCryptoPair)
            viewModel.getOrderBookData(searchCryptoPair)
        }
    }

    fun searchOperation(){
        searchCryptoPair = autoCompleteTextView.text.toString()
        if (searchCryptoPair.isNotEmpty() ){
            if(AppConstants.supportedCryptoValue.any{ it == searchCryptoPair}){
                closeKeyboard(textInputLayout)
                makeVisibilityGone(llOrderList)
                viewModel.getTickerData(searchCryptoPair)
            }else{
                showToast(getString(R.string.enter_valid_currency))
            }
        }else{
            showToast(getString(R.string.enter_currency))
        }
    }

    private fun observeViewModel() {
        viewModel.loadingState.observe(this, {
            if(it)
                makeVisibilityVisible(progressBar)
            else
                makeVisibilityGone(progressBar)
        })

        viewModel.response.observe(this, { response ->
            if(response.success) {
                if(response.requestId == viewModel.orderBookRequestId){
                    val orderBookResponse = response.data as OrderBookResponse
                    setUiForOrderList(orderBookResponse)
                }else if (response.requestId == viewModel.tickerRequestId){
                    val tickerDataResponse = response.data as TickerDataResponse
                    setUiForTicker(tickerDataResponse)
                }
            } else {
                if(response.requestId == viewModel.tickerRequestId) {
                    makeVisibilityGone(rlTicker)
                }
                response.error?.errorMessage?.let { showToast(it) }
            }
        })
    }

    private fun setUiForOrderList(orderBookResponse: OrderBookResponse) {
        println("Output orderBookRequestId: $orderBookResponse")
        val items = getFormattedOrderBookData(orderBookResponse)

        if(!items.isNullOrEmpty()){
            makeVisibilityVisible(llOrderList)
            orderAdapter.submitList(items)
        } else{
            makeVisibilityGone(llOrderList)
            showToast(getString(R.string.no_open_order))
        }
    }

    private fun getFormattedOrderBookData(orderBookResponse: OrderBookResponse) : List<OrderBookItem> {
        //data is already sorted so no sorting logic added

        //trim for long value
        val items = mutableListOf<OrderBookItem>()

        val bids: List<Any> = orderBookResponse.bids
        val asks: List<Any> = orderBookResponse.asks

        if(bids == null || asks == null) return items

        var i = 0
        while(i < 5) {
            val bid: List<*> = bids[i] as List<*>
            val ask: List<*> = asks[i] as List<*>
            items.add(
                OrderBookItem(
                    orderBookResponse.timestamp,
                    Bid(bid[0].toString(), bid[1].toString()),
                    Ask(ask[0].toString(), ask[1].toString())
                )
            )
            i++
        }

        return items
    }

    private fun setUiForTicker(tickerDataResponse: TickerDataResponse) {
        println("tickerDataResponse : $tickerDataResponse")

        makeVisibilityGone(llSearchText)
        makeVisibilityVisible(rlTicker)
        makeVisibilityVisible(floatingActionButton)
        binding.tickerResponse = tickerDataResponse


        binding.tvDate.text = AppConstants.epochToIso8601(tickerDataResponse.timestamp)
        binding.tvCurrencyPairName.text = searchCryptoPair.toUpperCase(Locale.ROOT)
    }

    private fun showToast(msg:String){
        if (::toast.isInitialized) {
            toast.cancel()
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.show()
    }


    private fun closeKeyboard(view: View) {
        view.clearFocus()
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun makeVisibilityVisible(view:View){
        if (!view.isVisible){
            view.visibility = View.VISIBLE
        }
    }

    private fun makeVisibilityGone(view:View){
        if (view.isVisible){
            view.visibility = View.GONE
        }
    }
}