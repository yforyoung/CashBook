package com.yyl.cashbook.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.yyl.cashbook.fragments.BookingFragment
import com.yyl.cashbook.fragments.ReportFragment

class HomeViewPagerAdapter(fm: FragmentManager, i: Int) : FragmentPagerAdapter(fm, i) {
    private val list = arrayOf(BookingFragment(), ReportFragment())

    override fun getItem(position: Int): Fragment = list[position]


    override fun getCount(): Int =list.size

    override fun getPageTitle(position: Int): CharSequence? {
       return when(position){
            0->"记账"
            1->"报表"
           else -> ""
       }
    }
}