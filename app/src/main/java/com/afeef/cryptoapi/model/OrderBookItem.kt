package com.afeef.cryptoapi.model

data class Bid(
    val price: String,
    val amount: String
)

data class Ask(
    val price: String,
    val amount: String
)

data class OrderBookItem(
    val timestamp: String,
    val bid: Bid,
    val ask: Ask
)