package com.yyl.cashbook.model.beans



const val food="餐饮"
const val play="娱乐"
const val trans="交通"
const val other="其他"
const val buy="购物"

class BookingType {
    var name=""
    var icon= 0

    constructor(name: String, icon: Int) {
        this.name = name
        this.icon = icon
    }
}