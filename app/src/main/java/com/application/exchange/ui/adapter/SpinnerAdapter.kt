package com.application.exchange.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.application.core.data.local.ExchangeEntity
import com.application.exchange.R

class SpinnerAdapter(private val mContext: Context, var list: List<ExchangeEntity>) :
    ArrayAdapter<ExchangeEntity>(mContext, R.layout.spinner_item_layout, list) {

    override fun getView(position: Int, rowView: View?, parent: ViewGroup): View {
        val convertView: View
        val viewHolder: ViewHolder
        // Check if an existing view is being reused, otherwise inflate the view
        if (rowView == null) {
            val inflator = LayoutInflater.from(mContext)
            convertView = inflator.inflate(R.layout.spinner_item_layout, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            convertView = rowView
            viewHolder = convertView.tag as ViewHolder
        }
        val entity = list[position]
        setValues(viewHolder, entity)
        return convertView
    }


    private fun setValues(
        viewHolder: ViewHolder,
        exchange: ExchangeEntity
    ) {
        viewHolder.exchangeTitle.text = exchange.symbol
    }

    // View lookup cache
    private class ViewHolder(view: View) {
        val exchangeTitle: TextView = view.findViewById(R.id.txt_symbol)
    }


}

