package com.emotaxi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emotaxi.R
import com.emotaxi.model.CardListModel

class CardListAdapter : RecyclerView.Adapter<CardListAdapter.MyViewHolder> {
    var arrayList: ArrayList<CardListModel.Cardinfo>? = null
    var context: Context? = null
    var setonCloseListener: SetOnCloseListener? = null
    var selectPosition = -1;

    constructor(
        context: Context,
        arrayList: ArrayList<CardListModel.Cardinfo>,
        setonCloseListener: SetOnCloseListener
    ) {
        this.context = context
        this.arrayList = arrayList
        this.setonCloseListener = setonCloseListener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_card_type: TextView = itemView.findViewById(R.id.tv_card_type)
        var checkbox: ImageView = itemView.findViewById(R.id.checkbox)
        var tv_cvv: TextView = itemView.findViewById(R.id.tv_cvv)
        var tv_card_date: TextView = itemView.findViewById(R.id.tv_card_date)
        var tv_card_number: TextView = itemView.findViewById(R.id.tv_card_number)
        var image_close: ImageView = itemView.findViewById(R.id.image_close)
        var btn_pay: Button = itemView.findViewById(R.id.btn_pay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_layout, parent, false)
        return MyViewHolder((itemView))
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_card_type.text = arrayList!![position].brand
        holder.tv_card_number.text = "XXXX-XXXX-XXXX-" + arrayList!![position].last4
        holder.tv_card_date.text =
            arrayList!![position].expMonth.toString() + "/" + arrayList!![position].expYear.toString()
        holder.tv_cvv.text = arrayList!![position].cvcCheck

        holder.btn_pay.setOnClickListener {
            setonCloseListener!!.onCloseListener(arrayList!![position], false, position)
        }
        if (selectPosition == position) {
            holder.checkbox.setImageResource(R.drawable.ic_baseline_check_circle_24)
        } else {
            holder.checkbox.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
        }
        holder.image_close.setOnClickListener {
            arrayList!!.removeAt(position)
            notifyDataSetChanged()
            setonCloseListener!!.onCloseListener(arrayList!![position], true, position)
        }

        holder.checkbox.setOnClickListener {
            selectPosition = position
            notifyDataSetChanged()
        }
    }

    interface SetOnCloseListener {
        fun onCloseListener(cardinfo: CardListModel.Cardinfo, clickOnView: Boolean, position: Int)
    }

}