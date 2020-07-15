package com.yyl.cashbook.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yyl.cashbook.R
import com.yyl.cashbook.`interface`.OnItemClickListener
import com.yyl.cashbook.activity.BookingDetailActivity
import com.yyl.cashbook.adapter.BillAdapter
import com.yyl.cashbook.database.Bill
import com.yyl.cashbook.database.DailyBill
import com.yyl.cashbook.model.CashbookModel
import com.yyl.cashbook.model.beans.Cashbook
import com.yyl.cashbook.utils.*
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BookingFragment : Fragment() {
    private lateinit var adapter: BillAdapter
    private val list = arrayListOf<Cashbook>()
    private val cashbookModel = CashbookModel()
    private lateinit var context: FragmentActivity
    private var page = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        EventBus.getDefault().register(this)
        context = activity!!
        adapter = BillAdapter(list)
        recycler_view.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        recycler_view.adapter = adapter
        recycler_view.itemAnimator = SlideInLeftAnimator()

        initListener()
        initData()
    }

    private fun initData() {        //初始化数据
        GlobalScope.launch(IO) {
            val todayCount = cashbookModel.getTodayCount()
            val monthCount = cashbookModel.getMonthCount()
            list.clear()
            cashbookModel.getCashbookList(page)?.let {
                list.addAll(it)
                withContext(Main) {
                    tv_day_out.text = todayCount
                    tv_month_out.text = monthCount
                    adapter.notifyItemRangeChanged(0, list.size - 1)
                }
            }
            page++
        }
    }


    private fun initListener() {
        ll_booking_add_bt.setOnClickListener {
            context.jump2Activity<BookingDetailActivity>(context)
        }

        /*下拉加载更多*/
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val itemCount = layoutManager.itemCount
                val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastPosition == itemCount - 1 && itemCount > 10) {
                    Log.i("yforyoung", "最后一个啦，去加载更多吧 page:$page")
                    val start = list.size
                    cashbookModel.getCashbookList(page)?.let {
                        if (it.isNotEmpty()) {
                            list.addAll(it)
                        }
                    }
                    page++;
                    adapter.notifyItemRangeInserted(start, list.size - 1)
                }
            }
        })

        /*点击事件*/
        adapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemDelete(v: View, position: Int) {
                //删除
                cashbookModel.deleteCashbook(list[position].item as Bill)
                /**
                 *  获取删除的那条数据的日期在list中的位置
                 * 获取到金额<0的时候就删除日期数据
                 */
                val holder =
                    recycler_view.findViewHolderForAdapterPosition(position) as BillAdapter.ViewHolder
                refreshCashData(DELETE_CASH, position, holder.parentPosition!!)
            }
        }
        /*显示一个删除 关闭另一个删除*/
        adapter.handlerAnotherView = object : BillAdapter.HandlerAnotherView {
            override fun handleAnother(position: Int) {
                recycler_view.getChildAt(position)!!
                    .findViewById<ImageView>(R.id.iv_item_bill)
                    .performClick()
            }
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBillAdd(bill: Bill) {
        /**
         *添加了账单
         * 如果添加的日期包含在当前已经加载出来的日期中
         * 直接添加 并且滑动到这里
         *
         * 否则刷新所有数据
         * 再滑动到position
         * */
        for (i in list) {

        }
//        refreshCashData(ADD_CASH,)
    }

    /**
     * 每次查询显示前10个日期的记录
     * 添加时计算出位置
     * 添加到对应的位置
     *
     * */
    private fun refreshCashData(action: Int, position: Int, parentPosition: Int) {
        GlobalScope.launch(IO) {
            val todayCount = cashbookModel.getTodayCount()
            val monthCount = cashbookModel.getMonthCount()
            withContext(Main) {
                if (action == DELETE_CASH) {
                    val dailyBill = list[parentPosition].item as DailyBill
                    dailyBill.dayCount -= (list[position].item as Bill).count
                    list.removeAt(position)
                    if (dailyBill.dayCount <= 0) {
                        list.removeAt(parentPosition)
                    }
                } else {
                    cashbookModel.getCashbookList(page)?.let {
                        list.addAll(it)
                        withContext(Main) {
                            tv_day_out.text = todayCount
                            tv_month_out.text = monthCount
                            adapter.notifyItemRangeChanged(0, list.size - 1)
                        }
                    }
                    page++
                }

                adapter.notifyDataSetChanged()
                tv_day_out.text = todayCount
                tv_month_out.text = monthCount
            }
        }
    }
}