package com.yyl.cashbook.model

import com.yyl.cashbook.database.Bill
import com.yyl.cashbook.database.DailyBill
import com.yyl.cashbook.model.beans.Cashbook
import com.yyl.cashbook.model.beans.TYPE_BILL
import com.yyl.cashbook.model.beans.TYPE_DAILY_COUNT
import com.yyl.cashbook.utils.getDateIntByDate
import com.yyl.cashbook.utils.getTodayString
import com.yyl.cashbook.utils.log
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

        if (billList.isEmpty()) {
            return emptyList()
        }
        val dailyBillList = arrayListOf<DailyBill>()
        val dailyBill = DailyBill()
        dailyBill.dayCount = billList[0].count
        dailyBill.date = billList[0].date
        dailyBillList.add(dailyBill)

        for (i in 1 until billList.size) {
            if (dailyBillList[dailyBillList.size - 1].date == billList[i].date) {
                dailyBillList[dailyBillList.size - 1].dayCount = DecimalFormat("0.00")
                    .format(dailyBillList[dailyBillList.size - 1].dayCount + billList[i].count)
                    .toDouble()
            } else {
                val db = DailyBill()
                db.dayCount = billList[i].count
                db.date = billList[i].date
                dailyBillList.add(db)
            }
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

    /*9 9 7 7 6 6*/
    //查找左边界的二分查找  逆序
    private fun binarySearch(key: Int, list: ArrayList<Int>): Int {
        var left = 0
        var right = list.size
        while (left < right) {
            val position = (right + left) / 2
            val midData = list[position]
            if (midData > key) {
                left = position + 1
            } else {
                right = position
            }
        }
        if (left < list.size - 1 && list[left + 1] < key) {
            return left     //如果不存在这个数 返回值就是需要的值
        } else {
            return left + 1     //如果存在  需要+1
        }
    }
}