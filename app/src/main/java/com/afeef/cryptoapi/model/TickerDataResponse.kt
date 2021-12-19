package com.afeef.cryptoapi.model

import com.afeef.cryptoapi.network.common.Data
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TickerDataResponse(
    val volume: String,
    val last: String,
    val timestamp: String,
    val bid: String,
    val vwap: String,
    val high: String,
    val low: String,
    val ask: String,
    val open: String
) : Data
