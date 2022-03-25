package com.example.kviewsample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvKLine = findViewById<TextView>(R.id.tv_main_k_line)
        val tvKCandle = findViewById<TextView>(R.id.tv_main_k_candle)

        tvKLine.setOnClickListener {
            KLineActivity.starts(this)
        }
        tvKCandle.setOnClickListener {
            KCandleActivity.starts(this)
        }


    }
}