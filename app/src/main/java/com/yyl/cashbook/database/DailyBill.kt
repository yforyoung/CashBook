package com.yyl.cashbook.database

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

/**
 *  每日账单
 *
 * 日期 （日期唯一）
 * 金额 （总额）
 * */
class DailyBill : LitePalSupport() {
    @Column(unique = true)
    var date = 19900101
    var dayCount = 0.00
}