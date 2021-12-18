package com.afeef.cryptoapi.util

import kotlin.collections.ArrayList

class AppConstants {
    companion object {
        const val APP_NAME = "CryptoApp"

        const val BASE_URL = "https://www.bitstamp.net/api/v2/"


        var supportedCryptoValue = arrayListOf("btcusd","btceur","btcgbp","btcpax","btcusdc","gbpusd","gbpeur","eurusd","xrpusd","xrpeur","xrpbtc","xrpgbp","xrppax","ltcusd","ltceur","ltcbtc","ltcgbp","ethusd","etheur","ethbtc","ethgbp","ethpax","ethusdc","bchusd","bcheur","bchbtc","bchgbp","paxusd","paxeur","paxgbp","xlmbtc","xlmusd","xlmeur","xlmgbp","linkusd","linkeur","linkgbp","linkbtc","linketh","omgusd","omgeur","omggbp","omgbtc","usdcusd","usdceur")


        /*fun getSupportedValueList() : ArrayList<String>{
            var list :ArrayList<String> = ArrayList()

            if (!supportedCryptoValue.isNullOrEmpty()){ //null check
                list = supportedCryptoValue.toCollection(ArrayList())
            }
            return list
        }*/


    }
}