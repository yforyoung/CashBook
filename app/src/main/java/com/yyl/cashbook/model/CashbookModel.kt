package com.yyl.cashbook.model

import com.yyl.cashbook.database.Bill
import com.yyl.cashbook.database.DailyBill
import com.yyl.cashbook.model.beans.Cashbook
import com.yyl.cashbook.model.beans.TYPE_BILL
import com.yyl.cashbook.model.beans.TYPE_DAILY_COUNT
import com.yyl.cashbook.utils.getDateIntByDate
import com.yyl.cashbook.utils.getTodayString
import com.yyl.cashbook.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litepal.LitePal
import org.litepal.extension.sum
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class CashbookModel {
    fun getCashbookList(): List<Cashbook>? {
        val billList = LitePal.order("date desc, id desc").find(Bill::class.java)
        val dailyBillList = arrayListOf<DailyBill>()
        val cursor =
            LitePal.findBySQL("select date,sum(count) from Bill group by date order by date desc, id desc")
        while (cursor.moveToNext()) {
            val dailyBill = DailyBill()
            dailyBill.dayCount = cursor.getDouble(1)
            dailyBill.date = cursor.getInt(0)
            dailyBillList.add(dailyBill)
        }

        return merge(billList, dailyBillList)
    }

    fun getCashbookList(page: Int): List<Cashbook>? {
        val billList = LitePal
            .limit(10)
            .offset(page * 10)
            .order("date desc, id desc")
            .find(Bill::class.java)

        val dailyBillList = arrayListOf<DailyBill>()
        val cursor =
            LitePal.findBySQL("select date,sum(count) from Bill group by date order by date desc, id desc")
        while (cursor.moveToNext()) {
            val dailyBill = DailyBill()
            dailyBill.dayCount = cursor.getDouble(1)
            dailyBill.date = cursor.getInt(0)
            dailyBillList.add(dailyBill)
        }
        return merge(billList, dailyBillList)
    }


    private fun merge(billList: List<Bill>, dailyBillList: List<DailyBill>): List<Cashbook> {
        val list = arrayListOf<Cashbook>()
        val p = billList.size
        val q = dailyBillList.size
        var i = 0
        var j = 0
        while (i < p && j < q) {
            if (list.isEmpty()) {
                list.add(Cashbook(dailyBillList[j], TYPE_DAILY_COUNT))
            } else if (billList[i].date != dailyBillList[j].date) {
                j++
                list.add(Cashbook(dailyBillList[j], TYPE_DAILY_COUNT))
            }
            list.add(Cashbook(billList[i], TYPE_BILL))
            i++
        }
        //应该只会出现j==q的情况
        if (j == q) {
            while (i < p) {
                list.add(Cashbook(billList[i], TYPE_BILL))
                i++
                log(list[i].type.toString())
            }
        }
        return list
    }

    fun getTodayCount(): String {
        val count = LitePal
            .where("date = ?", getTodayString())
            .sum<Bill, Double>("count")
        return DecimalFormat("0.00").format(count).toString()
    }

    fun getMonthCount(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val count = LitePal
            .where(
                "date >= ? and date <= ?",
                getDateIntByDate(calendar.time).toString(),
                getTodayString()
            )
            .sum<Bill, Double>("count")
        return DecimalFormat("0.00").format(count).toString()
    }

    fun addCashbook(b: Bill) {
        b.save()
    }

    fun deleteCashbook(b: Bill) {
        b.delete()
    }

    fun updateCashbook(b: Bill) {
        b.save()
    }

    fun findPosition(b: Bill, list: ArrayList<Cashbook>): Int {
        val dateList = arrayListOf<Int>()
        for (c in list) {
            if (c.item is DailyBill) {
                dateList.add((c.item as DailyBill).date)
            } else if (c.item is Bill) {
                dateList.add((c.item as Bill).date)
            }
        }
        return binarySearch(b.date, dateList)
    }

    /*1 1 1 2 2 2 2 3 3 4 4 5 5 8 8 9 9*/
    /*2--5*/
    /*7--13*/
    fun binarySearch(key: Int, list: ArrayList<Int>): Int {
        var position = list.size / 2
        var midData = list[position]
        while (true) {
            if (midData == key) {
                //向前找 找到第一个等于key的位置
                if (position == 0 || position == 1) {
                    return 1
                }
                while (position > 1) {
                    if (list[position - 1] < key) { //前一个值<key position就是第一个 ，因此返回position+1
                        return position + 1
                    } else if (list[position - 1] == key) { //前一个值=key  继续向前找
                        position--
                    }
                }
            } else if (midData > key) {
                position /= 2
            } else {
                position = (list.size - position) / 2
            }
        }
    }
}