package com.yyl.cashbook.model

import com.yyl.cashbook.R
import com.yyl.cashbook.model.beans.*

class TypeModel {

    fun getTypeList(): List<BookingType> {
        val list = arrayListOf<BookingType>()
        list.add(BookingType(food, R.drawable.pic_food))
        list.add(BookingType(trans, R.drawable.pic_trans))
        list.add(BookingType(play, R.drawable.pic_play))
        list.add(BookingType(buy, R.drawable.pic_buy))
        list.add(BookingType(other, R.drawable.pic_others))
        return list
    }
}