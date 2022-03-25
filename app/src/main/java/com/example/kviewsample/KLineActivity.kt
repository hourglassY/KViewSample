package com.example.kviewsample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.k_view.IKViewScaleChangedCallback
import com.example.k_view.KCandleItemBean
import com.example.k_view.KLinChartView
import com.example.k_view.KRightTextView
import kotlin.random.Random


class KLineActivity : AppCompatActivity() {

    private lateinit var mKLineView: KLinChartView
    private lateinit var mKRightText: KRightTextView
    private var mLastEndValue = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_k_line)
        mKLineView = findViewById(R.id.k_line_chart_view)
        mKRightText = findViewById(R.id.k_right_text)

        mKLineView.setKViewScaleCallback(
            object : IKViewScaleChangedCallback {
                override fun onEndValue(value: Float) {
                    mLastEndValue = value
                    mKRightText.setCurrentValue(value)
                }

                override fun onMaxAndMinScale(
                    maxValue: Float, maxValueInt: Int,
                    minValue: Float, minValueInt: Int, itemCount: Int
                ) {
                    mKRightText.setMaxAndMinValue(
                        maxValue, maxValueInt, minValue, minValueInt, itemCount
                    )
                }
            })
        initFlowLooper()

        setData()
    }


    private fun initFlowLooper() {
        lifecycleScope.launchWhenResumed {
            flowLooper(
                onStart = {

                },
                onCollect = {
                    mKLineView.addData(getBeanNormal())
                })

        }
    }

    private fun setData() {
        val data = mutableListOf<KCandleItemBean>()
        for (pos in 0..400) {
            val bean = getBean()
            data.add(bean)
        }
        mKLineView.setData(data)
    }

    private fun getBean(): KCandleItemBean {
        val openPrice = Random.nextInt(60) + 32900.0000f
        return KCandleItemBean(
            openPrice, openPrice, openPrice, openPrice,
            time = "16:21:21", timestamp = ""
        )
    }

    private fun getBeanNormal(): KCandleItemBean {
        val ccc = Random.nextInt(100)
        if (mLastEndValue == 0f) {
            mLastEndValue = 32900.0000f
        }
        val openPrice = if (ccc % 2 == 0) {
            mLastEndValue + Random.nextInt(15)
        } else {
            mLastEndValue - Random.nextInt(15)
        }

        return KCandleItemBean(
            openPrice, openPrice, openPrice, openPrice,
            time = "16:21:21", timestamp = ""
        )
    }

    companion object{
        fun starts(context: Context){
            context.startActivity(Intent(context, KLineActivity::class.java))
        }
    }

}