package com.yyl.cashbook.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*

/**
 * 2020-01-01
 */
val Main = Dispatchers.Main
val IO = Dispatchers.IO

fun getDateStringByInt(i: Int): String {
    val date = SimpleDateFormat("yyyyMMdd").parse(i.toString())
    val today = Date()
    val smp: SimpleDateFormat
    smp = when {
        today.year > date.year -> {
            SimpleDateFormat("yyyy年MM月dd日")
        }
        else -> {
            SimpleDateFormat("MM年dd日")
        }
    }
    return smp.format(date)
}


/**
 * 数据库使用
 * 20200101
 * */
fun getTodayString(): String {
    val date = Date()
    return SimpleDateFormat("yyyyMMdd").format(date)
}

/**
 * 20200101
 */
fun getDateIntByDate(date: Date): Int {
    return SimpleDateFormat("yyyyMMdd").format(date).toInt()
}

fun Any.log(s: String) {
    Log.i("yforyoung", s)
}

inline fun <reified T> Context.jump2Activity(from: Context) {
    from.startActivity(Intent(from, T::class.java))
}
