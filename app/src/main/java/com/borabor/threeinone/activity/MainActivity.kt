package com.borabor.threeinone.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.borabor.threeinone.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        btCalculator.setOnClickListener {
            startActivity(Intent(this, CalculatorActivity::class.java))
        }

        btConverter.setOnClickListener {
            startActivity(Intent(this, UnitConverterActivity::class.java))
        }

        btTimer.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }
    }
}