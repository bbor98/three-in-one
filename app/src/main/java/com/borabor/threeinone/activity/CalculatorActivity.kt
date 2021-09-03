package com.borabor.threeinone.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.borabor.threeinone.R
import com.borabor.threeinone.adapter.ViewPagerAdapter
import com.borabor.threeinone.fragment.BasicCalculatorFragment
import com.borabor.threeinone.fragment.ScientificCalculatorFragment
import kotlinx.android.synthetic.main.activity_calculator.*

class CalculatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val actionBar = supportActionBar
        actionBar!!.title = getString(R.string.calculator)
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
        adapter.addFragment(BasicCalculatorFragment(), getString(R.string.basic_calculator))
        adapter.addFragment(ScientificCalculatorFragment(), getString(R.string.scientific_calculator))
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}