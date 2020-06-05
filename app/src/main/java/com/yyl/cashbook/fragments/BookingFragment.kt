package com.yyl.cashbook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yyl.cashbook.R
import com.yyl.cashbook.`interface`.OnItemClickListener
import com.yyl.cashbook.activity.BookingDetailActivity
import com.yyl.cashbook.adapter.BillAdapter
import com.yyl.cashbook.database.Bill
import com.yyl.cashbook.model.CashbookModel
import com.yyl.cashbook.model.beans.Cashbook
import com.yyl.cashbook.utils.IO
import com.yyl.cashbook.utils.Main
import com.yyl.cashbook.utils.jump2Activity
import com.yyl.cashbook.utils.log
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingFragment : Fragment() {
    private lateinit var adapter: BillAdapter
    private val list = arrayListOf<Cashbook>()
    private val cashbookModel = CashbookModel()
    private lateinit var context: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context = activity!!
        adapter = BillAdapter(list)
        adapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                /*(v as ScrollView).fullScroll(ScrollView.FOCUS_DOWN)*/
                //删除
                adapter.deleteShowingPosition = -1
                cashbookModel.deleteCashbook(list[position].item as Bill)
                refreshCashData()
            }
        }
        adapter.handlerAnotherView = object : BillAdapter.HandlerAnotherView {
            override fun handleAnother(position: Int) {
                recycler_view.getChildAt(position)!!
                    .findViewById<ImageView>(R.id.iv_item_bill)
                    .performClick()
            }

        }
        recycler_view.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        recycler_view.adapter = adapter
        recycler_view.itemAnimator=SlideInLeftAnimator()
        initListener()
    }


    private fun initListener() {
        ll_booking_add_bt.setOnClickListener {
            context.jump2Activity<BookingDetailActivity>(context)
        }

    }

    override fun onResume() {
        super.onResume()
        refreshCashData()

    }

    private fun refreshCashData() {
        GlobalScope.launch(IO) {
            val todayCount=cashbookModel.getTodayCount()
            val monthCount=cashbookModel.getMonthCount()
            list.clear()
            list.addAll(cashbookModel.getCashbookList()!!)
            withContext(Main){
                tv_day_out.text = todayCount
                tv_month_out.text = monthCount
                adapter.notifyItemRangeChanged(0,list.size-1)
            }
        }
//        tv_day_out.text = cashbookModel.getTodayCount()
//        tv_month_out.text = cashbookModel.getMonthCount()
//        adapter.notifyDataSetChanged()
    }
}