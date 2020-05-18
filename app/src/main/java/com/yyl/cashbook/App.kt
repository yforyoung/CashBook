package com.yyl.cashbook

import android.app.Application
import org.litepal.LitePal

/*
* 先实现功能
* 别想骚操作
*
* */

const val TAG="yforyoung"
class App:Application(){
    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
    }
}