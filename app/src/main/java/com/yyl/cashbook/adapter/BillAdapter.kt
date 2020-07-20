package com.yyl.cashbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.recyclerview.widget.RecyclerView
import com.yyl.cashbook.R
import com.yyl.cashbook.`interface`.OnItemClickListener
import com.yyl.cashbook.database.Bill
import com.yyl.cashbook.database.DailyBill
import com.yyl.cashbook.model.beans.*
import com.yyl.cashbook.utils.getDateStringByInt
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder
import kotlinx.android.synthetic.main.item_bill_list.view.*
import kotlinx.android.synthetic.main.item_daily_count.view.*

class BillAdapter(private val list: List<Cashbook>) :
    RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var deleteShowingPosition = -1
    var handlerAnotherView: HandlerAnotherView? = null
    lateinit var deleteAnimation: Animation

    private var mParentPosition: Int? = null      //用于标识日期所在的位置

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentPosition: Int? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = if (viewType == TYPE_BILL) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bill_list, parent, false)
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
            holder.parentPosition = mParentPosition
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
                if (position != deleteShowingPosition) {
                    iv_item_bill_delete.visibility = View.GONE;
                }

                /************点击事件***************/
                //点击图标切换删除按钮
                iv_item_bill.tag = position
                iv_item_bill.setOnClickListener {
                    if (iv_item_bill_delete.visibility != View.VISIBLE) {
                        val taShow = TranslateAnimation(
                            right.toFloat(),
                            0f,
                            iv_item_bill_delete.translationY,
                            iv_item_bill_delete.translationY
                        )
                        taShow.duration = 500
                        iv_item_bill_delete.visibility = View.VISIBLE
                        if (deleteShowingPosition != -1 && deleteShowingPosition != position) {
                            handlerAnotherView!!.handleAnother(deleteShowingPosition)
                        }
                        deleteShowingPosition = position
                        iv_item_bill_delete.startAnimation(taShow)
                    } else {
                        val taDismiss = TranslateAnimation(
                            0f,
                            right.toFloat(),
                            iv_item_bill_delete.translationY,
                            iv_item_bill_delete.translationY
                        )
                        taDismiss.duration = 500
                        taDismiss.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationRepeat(animation: Animation?) {
                            }

                            override fun onAnimationEnd(animation: Animation?) {
                                iv_item_bill_delete.visibility = View.GONE
                                if (deleteShowingPosition == position) {
                                    deleteShowingPosition = -1
                                }
                                iv_item_bill_delete.translationX = 0f
                            }

                            override fun onAnimationStart(animation: Animation?) {
                            }

                        })
                        iv_item_bill_delete.startAnimation(taDismiss)
                    }
                }

                iv_item_bill_delete.tag = position
                iv_item_bill_delete.setOnClickListener {
                    deleteShowingPosition = -1;

                    onItemClickListener!!.onItemDelete(
                        iv_item_bill_delete,
                        iv_item_bill_delete.tag as Int
                    )
                }
            }
        } else {
            with(holder.itemView) {
                tag = position
                mParentPosition = position
                val dailyBill = list[position].item as DailyBill
                tv_item_date.text = getDateStringByInt(dailyBill.date)
                tv_item_daily_count.text = dailyBill.dayCount.toString()
            }
        }
    }


    interface HandlerAnotherView {
        fun handleAnother(position: Int)
    }
}