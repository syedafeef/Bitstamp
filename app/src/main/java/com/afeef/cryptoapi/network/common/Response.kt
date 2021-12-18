package com.afeef.cryptoapi.network.common

data class Response(
    val statusCode: Int,
    val success: Boolean,
    val data: Data?,
    val requestId : Int,
    val error: Error?
)