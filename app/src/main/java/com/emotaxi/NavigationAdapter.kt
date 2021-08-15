package com.emotaxi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NavigationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    var list: ArrayList<String>? = null

    constructor() {
        list = ArrayList<String>()
        list?.add("Header")
        list?.add("Profile")
        list?.add("Add card")
        list?.add("Setting")
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_title: TextView = itemView.findViewById(R.id.tv_title)
    }

    class MyHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemView: View? = null
        var holder: RecyclerView.ViewHolder? = null
        if (viewType == 0) {
            itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_header, parent, false)
            holder = MyHeaderViewHolder(itemView)
        } else {
            itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
            holder = MyViewHolder(itemView)
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {

        } else {
            (holder as MyViewHolder).tv_title.text = list?.get(position)
        }
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}