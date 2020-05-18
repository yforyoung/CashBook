package com.yyl.cashbook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yyl.cashbook.R
import com.yyl.cashbook.`interface`.OnItemClickListener
import com.yyl.cashbook.activity.BookingDetailActivity
import com.yyl.cashbook.adapter.BillAdapter
import com.yyl.cashbook.model.CashbookModel
import com.yyl.cashbook.model.beans.Cashbook
import com.yyl.cashbook.utils.jump2Activity
import kotlinx.android.synthetic.main.fragment_booking.*

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

            }
        }
        recycler_view.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        recycler_view.adapter = adapter

        initListener()
    }

    private fun initData() {
        tv_day_out.text = cashbookModel.getTodayCount()
        tv_month_out.text = cashbookModel.getMonthCount()
    }

    private fun initListener() {
        ll_booking_add_bt.setOnClickListener {
            context.jump2Activity<BookingDetailActivity>(context)
        }

    }

    override fun onResume() {
        super.onResume()
        initData()

        list.clear()
        list.addAll(cashbookModel.getCashbookList()!!)
        adapter.notifyDataSetChanged()
    }
}