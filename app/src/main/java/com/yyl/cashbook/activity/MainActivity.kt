package com.yyl.cashbook.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yyl.cashbook.R
import com.yyl.cashbook.adapter.HomeViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: HomeViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter= HomeViewPagerAdapter(supportFragmentManager,0)
        vp_home.adapter=adapter
        tab_main.setupWithViewPager(vp_home)
    }
}
