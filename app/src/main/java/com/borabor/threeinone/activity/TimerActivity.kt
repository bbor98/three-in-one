package com.borabor.threeinone.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.borabor.threeinone.R
import com.borabor.threeinone.adapter.ViewPagerAdapter
import com.borabor.threeinone.fragment.CountdownTimerFragment
import com.borabor.threeinone.fragment.CounterFragment
import com.borabor.threeinone.fragment.StopwatchFragment
import kotlinx.android.synthetic.main.activity_calculator.*

class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val actionBar = supportActionBar
        actionBar!!.title = getString(R.string.timer)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        setUpTabs()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(StopwatchFragment(), getString(R.string.stopwatch))
        adapter.addFragment(CountdownTimerFragment(), getString(R.string.countdown_timer))
        adapter.addFragment(CounterFragment(), getString(R.string.counter))
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}
