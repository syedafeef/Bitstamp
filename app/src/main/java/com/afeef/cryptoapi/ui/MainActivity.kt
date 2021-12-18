package com.afeef.cryptoapi.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import com.afeef.cryptoapi.R
import com.afeef.cryptoapi.model.OrderBookResponse
import com.afeef.cryptoapi.util.AppConstants
import com.afeef.cryptoapi.viewmodel.MainActivityViewModel
import com.google.android.material.textfield.TextInputLayout
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import com.afeef.cryptoapi.model.TickerDataResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var autoCompleteTextView :AutoCompleteTextView
    private lateinit var textInputlayout : TextInputLayout
    private lateinit var rl_ticker : RelativeLayout
    lateinit var toast:Toast

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

    var searchcryptoPair = ""

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
        floatingActionButton = findViewById(R.id.fab_refresh)


        tv_currency_pair = findViewById(R.id.tv_currency_pair_name)
        tv_date = findViewById(R.id.tv_date)
        tv_open = findViewById(R.id.tv_open_value)
        tv_low = findViewById(R.id.tv_low_value)
        tv_volume = findViewById(R.id.tv_volume_value)
        tv_high = findViewById(R.id.tv_high_value)
        tv_last = findViewById(R.id.tv_last_value)

        tv_view_hide_order_book = findViewById(R.id.tv_view_hide_order_book)

        val autoCompleteTvAdapter: ArrayAdapter<String> = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, AppConstants.supportedCryptoValue)
        autoCompleteTextView.setAdapter(autoCompleteTvAdapter)
    }

    private fun setClickListners() {
        textInputlayout.setEndIconOnClickListener{
            searchcryptoPair = autoCompleteTextView.text.toString()
            if (!searchcryptoPair.isEmpty()){
                viewModel.getTickerData(searchcryptoPair)
            }else{
                //show toast
                showToast("Enter a value")
            }
        }

        tv_view_hide_order_book.setOnClickListener {
            //hide and unhide UI
            showToast("hide and unhide")
        }

        floatingActionButton.setOnClickListener {
            showToast("FLoating button")
            viewModel.getTickerData(searchcryptoPair)
        }
    }




    private fun observeViewModel() {
        viewModel.loadingState.observe(this, Observer {
            if(it)
                progressBar.visibility = View.VISIBLE
            else
                progressBar.visibility = View.GONE
        })

        viewModel.response.observe(this, Observer {response ->
            if(response.success) {

                if(response.requestId == viewModel.orderBookRequestId){
                    val orderBookResponse = response.data as OrderBookResponse
                    println("Output orderBookRequestId: $orderBookResponse") //tickerDataResponse.timestamp

                }else if (response.requestId == viewModel.tickerRequestId){
                    val tickerDataResponse = response.data as TickerDataResponse


                    setUIforTicker(tickerDataResponse)
                }


//                val items = getFormattedOrderBookData(orderBookResponse)
//                if(!items.isNullOrEmpty())
            //                adapter.submitList(items)
//                else super.showErrorDialog(null)
            } else {
                rl_ticker.visibility = View.GONE
                response.error?.errorMessage?.let { showToast(it) }
            }
        })
    }

    private fun setUIforTicker(tickerDataResponse: TickerDataResponse) {
        println("tickerDataResponse : $tickerDataResponse") //tickerDataResponse.timestamp

        rl_ticker.visibility = View.VISIBLE
        floatingActionButton.visibility = View.VISIBLE

        tv_date.text = epochToIso8601(tickerDataResponse.timestamp.toLong())
        tv_currency_pair.text = searchcryptoPair.toUpperCase(Locale.ROOT)
        tv_open.text = "$ ${tickerDataResponse.open}"
        tv_low.text = "$ ${tickerDataResponse.low}"
        tv_volume.text = tickerDataResponse.volume
        tv_high.text = "$ ${tickerDataResponse.high}"
        tv_last.text = "$ ${tickerDataResponse.last}"
    }

    fun epochToIso8601(time: Long): String? {
        val format = "dd MMM yyyy HH:mm:ss"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.setTimeZone(TimeZone.getDefault())
        return sdf.format(Date(time * 1000))
    }

    fun showToast(msg:String){
        if (::toast.isInitialized) {
            toast.cancel()
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.show()
    }

}