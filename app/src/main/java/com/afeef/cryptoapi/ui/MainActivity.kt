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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var autoCompleteTextView :AutoCompleteTextView
    private lateinit var textInputlayout : TextInputLayout
    private lateinit var rl_ticker : RelativeLayout
    private lateinit var ll_orderlist : LinearLayout
    private lateinit var tv_currency_pair : TextView
    private lateinit var tv_date : TextView
    private lateinit var tv_open : TextView
    private lateinit var tv_low : TextView
    private lateinit var tv_volume : TextView
    private lateinit var tv_high : TextView
    private lateinit var tv_last : TextView
    private lateinit var tv_view_hide_order_book : TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var adapter: OrderBookListAdapter
    private lateinit var orderList: RecyclerView

    var searchcryptoPair = ""

    lateinit var toast:Toast
    lateinit var context: Context
    private val viewModel: MainActivityViewModel by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        initUi()
        setClickListners()
        observeViewModel()
    }

    private fun initUi() {
        autoCompleteTextView = findViewById(R.id.et_searchbar)
        textInputlayout = findViewById(R.id.searchInputlayout)

        progressBar = findViewById(R.id.pb_api)
        rl_ticker = findViewById(R.id.rl_ticker_detail)
        ll_orderlist = findViewById(R.id.ll_orderlist)
        floatingActionButton = findViewById(R.id.fab_refresh)


        tv_currency_pair = findViewById(R.id.tv_currency_pair_name)
        tv_date = findViewById(R.id.tv_date)
        tv_open = findViewById(R.id.tv_open_value)
        tv_low = findViewById(R.id.tv_low_value)
        tv_volume = findViewById(R.id.tv_volume_value)
        tv_high = findViewById(R.id.tv_high_value)
        tv_last = findViewById(R.id.tv_last_value)

        tv_view_hide_order_book = findViewById(R.id.tv_view_hide_order_book)
        orderList = findViewById(R.id.rv_orderList)

        val autoCompleteTvAdapter: ArrayAdapter<String> = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, AppConstants.supportedCryptoValue)
        autoCompleteTextView.setAdapter(autoCompleteTvAdapter)

        adapter = OrderBookListAdapter()
        orderList.adapter = adapter
    }

    private fun setClickListners() {
        textInputlayout.setEndIconOnClickListener{
            searchcryptoPair = autoCompleteTextView.text.toString()
            if (searchcryptoPair.isNotEmpty() && AppConstants.supportedCryptoValue.any{ it == searchcryptoPair}){
                closeKeyboard()
                viewModel.getTickerData(searchcryptoPair)
            }else{
                showToast(getString(R.string.enter_valid_currency))
            }
        }

        tv_view_hide_order_book.setOnClickListener {
            //hide and unhide UI

            if(ll_orderlist.isVisible){
                tv_view_hide_order_book.text = getString(R.string.view_order_book)
                makeVisibilityGone(ll_orderlist)
            }else{
                tv_view_hide_order_book.text = getString(R.string.hide_order_book)
                viewModel.getOrderBookData(searchcryptoPair)
            }

        }

        floatingActionButton.setOnClickListener {
            viewModel.getTickerData(searchcryptoPair)
            viewModel.getOrderBookData(searchcryptoPair)
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
                    makeVisibilityGone(rl_ticker)
                }
                response.error?.errorMessage?.let { showToast(it) }
            }
        })
    }

    private fun setUiForOrderList(orderBookResponse: OrderBookResponse) {
        println("Output orderBookRequestId: $orderBookResponse")
        val items = getFormattedOrderBookData(orderBookResponse)

        if(!items.isNullOrEmpty()){
            makeVisibilityVisible(ll_orderlist)
            adapter.submitList(items)
        } else{
            makeVisibilityGone(ll_orderlist)
            showToast(getString(R.string.no_open_order))
        }
    }

    private fun getFormattedOrderBookData(orderBookResponse: OrderBookResponse) : List<OrderBookItem> {
        //data is already sorted so no sorting logic added

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
        println("tickerDataResponse : $tickerDataResponse") //tickerDataResponse.timestamp

        makeVisibilityVisible(rl_ticker)
        makeVisibilityVisible(floatingActionButton)

        tv_date.text = epochToIso8601(tickerDataResponse.timestamp.toLong())
        tv_currency_pair.text = searchcryptoPair.toUpperCase(Locale.ROOT)
        tv_open.text = "$ ${tickerDataResponse.open}"
        tv_low.text = "$ ${tickerDataResponse.low}"
        tv_volume.text = tickerDataResponse.volume
        tv_high.text = "$ ${tickerDataResponse.high}"
        tv_last.text = "$ ${tickerDataResponse.last}"
    }

    fun epochToIso8601(time: Long): String {
        val format = "dd MMM yyyy HH:mm:ss"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(time * 1000))
    }


    fun showToast(msg:String){
        if (::toast.isInitialized) {
            toast.cancel()
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun closeKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun changeVisibility(view:View){
        if (view.isVisible){
            view.visibility = View.GONE
        }else{
            view.visibility = View.VISIBLE
        }
    }

    fun makeVisibilityVisible(view:View){
        if (!view.isVisible){
            view.visibility = View.VISIBLE
        }
    }

    fun makeVisibilityGone(view:View){
        if (view.isVisible){
            view.visibility = View.GONE
        }
    }
}