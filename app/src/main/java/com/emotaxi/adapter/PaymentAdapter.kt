package com.emotaxi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emotaxi.R

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.MyViewHolder>{

    constructor(){

    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_adapter,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }
}