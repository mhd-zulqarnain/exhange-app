package com.application.exchange.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.core.data.local.ExchangeEntity
import com.application.exchange.R

class ExchangeAdapter(private val list: List<ExchangeEntity>, private val context: Context) :
    RecyclerView.Adapter<ExchangeAdapter.ViewHolders>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.exchange_item_layout, parent, false)
        return ViewHolders(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolders, position: Int) {
        val item = list[position]
        holder.bindView(item)

    }

    class ViewHolders(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(item: ExchangeEntity) {
            view.findViewById<TextView>(R.id.txtAmount).text = "%.2f".format(item.convertedAmount)

            view.findViewById<TextView>(R.id.txtSymbol).text = item.symbol
        }


    }
}