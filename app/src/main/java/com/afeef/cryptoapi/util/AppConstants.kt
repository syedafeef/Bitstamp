package com.afeef.cryptoapi.util

import java.text.SimpleDateFormat
import java.util.*

object AppConstants {

    const val BASE_URL = "https://www.bitstamp.net/api/v2/"

    val supportedCryptoValue = arrayListOf("btcusd","btceur","btcgbp","btcpax","btcusdc","gbpusd","gbpeur","eurusd","xrpusd","xrpeur","xrpbtc","xrpgbp","xrppax","ltcusd","ltceur","ltcbtc","ltcgbp","ethusd","etheur","ethbtc","ethgbp","ethpax","ethusdc","bchusd","bcheur","bchbtc","bchgbp","paxusd","paxeur","paxgbp","xlmbtc","xlmusd","xlmeur","xlmgbp","linkusd","linkeur","linkgbp","linkbtc","linketh","omgusd","omgeur","omggbp","omgbtc","usdcusd","usdceur")

    @JvmStatic
    fun epochToIso8601(time: String): String {

        val format = "dd MMM yyyy HH:mm:ss" // you can add the format you need
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(time.toLong() * 1000))
    }
}