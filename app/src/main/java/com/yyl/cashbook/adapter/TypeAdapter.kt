package com.yyl.cashbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yyl.cashbook.R
import com.yyl.cashbook.`interface`.OnItemClickListener
import com.yyl.cashbook.model.beans.BookingType
import kotlinx.android.synthetic.main.item_booking_detail_type.view.*

class TypeAdapter(private val list: List<BookingType>) :
    RecyclerView.Adapter<TypeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    lateinit var onItemClickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_detail_type, parent, false)
        view.setOnClickListener {
            onItemClickListener!!.onItemClick(view, view.tag as Int)
        }

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        with(holder.itemView) {
            iv_type_icon.setBackgroundResource(list[position].icon)
            tv_type_text.text = list[position].name
        }

    }

}