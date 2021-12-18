package com.afeef.cryptoapi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afeef.cryptoapi.R
import com.afeef.cryptoapi.model.OrderBookItem
import kotlinx.android.synthetic.main.order_book_list_item.view.*

class OrderBookListAdapter :
    ListAdapter<OrderBookItem, OrderBookListAdapter.OrderBookItemViewHolder>(OrderBookDiffUtil()) {

    class OrderBookItemViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        var bidAmount: TextView = root.bidAmount
        var bidPrice: TextView = root.bidPrice
        var askAmount: TextView = root.askAmount
        var askPrice: TextView = root.askPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderBookItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return OrderBookItemViewHolder(inflater.inflate(R.layout.order_book_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: OrderBookItemViewHolder, position: Int) {
        getItem(position).let {
            holder.bidAmount.text = it.bid.amount
            holder.bidPrice.text = it.bid.price
            holder.askAmount.text = it.ask.amount
            holder.askPrice.text = it.ask.price
        }
    }
}