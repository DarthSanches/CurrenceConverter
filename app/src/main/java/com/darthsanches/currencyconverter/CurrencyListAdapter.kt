package com.darthsanches.currencyconverter

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class CurrencyListAdapter :
        RecyclerView.Adapter<CurrencyListAdapter.MyViewHolder>() {
    private var mainValue = 0f
    private var base = "EUR"
    private var currencies = LinkedHashMap<String, Float>()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameView: TextView = view.findViewById(R.id.currency_name)
        var valueView: EditText = view.findViewById(R.id.currency_value)
    }

    val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            mainValue = if(s.isNullOrBlank()) 0f else s.toString().toFloat()
            //notifyItemRangeChanged(2, currencies.size)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CurrencyListAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (holder.adapterPosition == 0) {
            holder.valueView.addTextChangedListener(textChangeListener)
            holder.nameView.text = base
            holder.valueView.requestFocus()
        } else {
            holder.nameView.text = currencies.keys.toList()[position-1]
            val value = currencies.values.toList()[position -1] * mainValue
            holder.valueView.setText(value.toString())
            holder.valueView.removeTextChangedListener(textChangeListener)
            holder.itemView.setOnClickListener {
                val pos = holder.adapterPosition
                base = holder.nameView.text.toString()
                notifyItemMoved(pos, 0)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }/* else {
            if (position == 0) {
                holder.valueView.addTextChangedListener(textChangeListener)
                holder.valueView.requestFocus()
            } else {
                val value = currencies.values.toList()[position-1] * mainValue
                holder.valueView.removeTextChangedListener(textChangeListener)
                holder.valueView.setText(value.toString())
            }
        }*/
    }

    override fun getItemCount() = if(currencies.isNullOrEmpty()) 1 else currencies.size + 1

    fun setData(data: RateResponse){
        if(data.base != base) return
        if(currencies.isNullOrEmpty()) {
            currencies = data.rates
            notifyDataSetChanged()
        }else{
            currencies = data.rates
            notifyItemRangeChanged(1, currencies.size)
        }
    }

    public fun getBase() = base
}