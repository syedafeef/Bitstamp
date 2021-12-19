package com.afeef.cryptoapi.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.afeef.cryptoapi.model.OrderBookItem

class OrderBookDiffUtil: DiffUtil.ItemCallback<OrderBookItem>() {
    override fun areItemsTheSame(oldItem: OrderBookItem, newItem: OrderBookItem): Boolean {
        return oldItem.timestamp == newItem.timestamp
    }

    override fun areContentsTheSame(oldItem: OrderBookItem, newItem: OrderBookItem): Boolean {
        return oldItem.timestamp == newItem.timestamp
    }
}