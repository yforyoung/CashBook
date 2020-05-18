package com.yyl.cashbook.database

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

/**
 * 账单（每一笔流水） 用于数据库记录
 *
 * 日期（用于获取日总额）
 * 类型
 * 金额
 * 备注
 * 收/支
 * 账户类型
 * （留出扩展）
 * */
class Bill : LitePalSupport() {
    @Column(nullable = false)
    var date = 19900101
    @Column(nullable = false)
    var type = ""
    @Column(nullable = false)
    var count = 0.00
    var detail = ""
    var moneyOut = true
    var accountType = "支付宝"

}