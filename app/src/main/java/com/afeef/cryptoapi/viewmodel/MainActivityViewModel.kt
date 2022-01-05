package com.afeef.cryptoapi.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afeef.cryptoapi.db.AppDatabase
import com.afeef.cryptoapi.network.BitStampApiService
import com.afeef.cryptoapi.network.NetworkException
import com.afeef.cryptoapi.network.common.ErrorType
import com.afeef.cryptoapi.network.common.Response
import com.afeef.cryptoapi.network.common.Error
import kotlinx.coroutines.*

class MainActivityViewModel(private val service: BitStampApiService): ViewModel() {

    private val _response = MutableLiveData<Response>()
    val response: LiveData<Response> get() = _response

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState
    var tickerRequestId :Int = 1010
    var orderBookRequestId :Int = 1001

    private val viewModelJob = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
//    var database : AppDatabase?

    init {
//        database = AppDatabase.getAppDataBase()
    }


    fun getTickerData(currencyPair:String) {
        _loadingState.postValue(true)
        ioScope.launch {
            try {
                val serviceResponse = service.getTickerHistory(currencyPair)

                _loadingState.postValue(false)
                if(serviceResponse.isSuccessful) {
                    _response.postValue(Response(serviceResponse.code(), true, serviceResponse.body(),tickerRequestId,  null))
                } else {
                    handleError(null, tickerRequestId )
                }
            }
            catch (e : Exception) {
                _loadingState.postValue(false)
                handleError(e, tickerRequestId)
            }
        }
    }


    fun getOrderBookData(currencyPair:String) {
        _loadingState.postValue(true)

        ioScope.launch{
            try {
                val serviceResponse = service.getOrderBook(currencyPair)

                _loadingState.postValue(false)
                if(serviceResponse.isSuccessful) {
                    _response.postValue(Response(serviceResponse.code(), true, serviceResponse.body(), orderBookRequestId, null))
                } else {
                    handleError(null, orderBookRequestId )
                }
            }
            catch (e : Exception) {
                _loadingState.postValue(false)
                handleError(e, orderBookRequestId)
            }
        }
    }

    private fun handleError(e : Exception?, requestId: Int) {
        val message = e?.message.toString()
        val type = if(e != null && e is NetworkException) ErrorType.NETWORK_ERROR else ErrorType.SERVER_ERROR
        _response.postValue(Response(400, false, null, requestId, Error(type, message)))
    }

}