package com.afeef.cryptoapi.network

import com.afeef.cryptoapi.model.OrderBookResponse
import com.afeef.cryptoapi.model.TickerDataResponse
import com.afeef.cryptoapi.network.interceptor.ConnectivityInterceptor
import com.afeef.cryptoapi.util.AppConstants
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface BitStampApiService {

    @GET("ticker/{currency_pair}")
    suspend fun getTickerHistory(@Path("currency_pair") currentPair:String) : Response<TickerDataResponse>

    @GET("order_book/{currency_pair}")
    suspend fun getOrderBook(@Path("currency_pair") currentPair:String) : Response<OrderBookResponse>


    companion object {
        operator fun invoke(connectivityInterceptor: ConnectivityInterceptor) : BitStampApiService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(BitStampApiService::class.java)
        }


    }
}