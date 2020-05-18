package com.yyl.cashbook.model.beans

/**
 * 账单
 * 用来做首页显示
 */

const val TYPE_DAILY_COUNT = 0;
const val TYPE_BILL = 1;

class Cashbook() {
    var item: Any? = null
    var type: Int = TYPE_BILL

    constructor(item: Any, type: Int) : this() {
        this.item = item
        this.type = type
    }
}