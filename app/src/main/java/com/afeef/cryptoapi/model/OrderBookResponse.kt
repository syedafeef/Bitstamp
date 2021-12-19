package com.afeef.cryptoapi.model

import com.afeef.cryptoapi.network.common.Data
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderBookResponse(
    val timestamp: String,
    val bids: List<Any>,
    val asks: List<Any>
) : Data