package com.yyl.cashbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yyl.cashbook.R
import com.yyl.cashbook.`interface`.OnItemClickListener
import com.yyl.cashbook.database.Bill
import com.yyl.cashbook.database.DailyBill
import com.yyl.cashbook.model.beans.*
import com.yyl.cashbook.utils.getDateStringByInt
import kotlinx.android.synthetic.main.item_bill_list.view.*
import kotlinx.android.synthetic.main.item_daily_count.view.*

class BillAdapter(private val list: List<Cashbook>) :
    RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = if (viewType == TYPE_BILL) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_bill_list, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_daily_count, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list[position].type == TYPE_BILL) {
            with(holder.itemView) {
                tag = position
                val bill = list[position].item as Bill
                tv_item_bill_type.text = bill.type
                iv_item_bill.setBackgroundResource(
                    when (bill.type) {
                        food -> R.drawable.pic_food
                        play -> R.drawable.pic_play
                        trans -> R.drawable.pic_trans
                        buy -> R.drawable.pic_buy
                        else -> R.drawable.pic_others
                    }
                )
                tv_item_bill_count.text = bill.count.toString()
                tv_item_bill_detail.text = bill.detail
            }
        } else {
            with(holder.itemView) {
                tag = position
                val dailyBill = list[position].item as DailyBill
                tv_item_date.text = getDateStringByInt(dailyBill.date)
                tv_item_daily_count.text = dailyBill.dayCount.toString()
            }
        }
    }
}