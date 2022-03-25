package com.example.kviewsample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.k_view.IKViewScaleChangedCallback
import com.example.k_view.KCandleItemBean
import com.example.k_view.KCandleView
import com.example.k_view.KRightTextView
import kotlin.random.Random


class KCandleActivity : AppCompatActivity() {

    private lateinit var mKCandleView: KCandleView
    private lateinit var mKRightText: KRightTextView
    private var mLastEndValue = 0f

    private var mCountUp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_k_candle)
        mKCandleView = findViewById(R.id.k_candle_view)
        mKRightText = findViewById(R.id.k_right_text_candle)

        mKCandleView.setKViewScaleCallback(
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
                    if (mCountUp == 15) {
                        mCountUp = 0
                        mKCandleView.addData(getBeanNormal())
                    } else {
                        mCountUp++
                        mKCandleView.changeEndDataCloseAdd(getEndCloseValue())
                    }
                })

        }
    }

    private fun setData() {
        val data = mutableListOf<KCandleItemBean>()
        for (pos in 0..400) {
            val bean = getBean()
            data.add(bean)
        }
        mKCandleView.setData(data)
    }

    private fun getBean(): KCandleItemBean {
        val openPrice = Random.nextInt(60) + 32900.0000f
        val closePrice = Random.nextInt(60) + 32900.0000f
        val maxClose = Random.nextInt(20) + Math.max(openPrice, closePrice)
        val minClose = Math.min(openPrice, closePrice) - Random.nextInt(20)
//            val value = mLastEndValue - Random.nextInt(10) - 10
        return KCandleItemBean(
            openPrice,
            closePrice,
            maxClose,
            minClose,
            time = "16:21:21",
            timestamp = ""
        )
    }

    private fun getBeanNormal(): KCandleItemBean {
        val openPrice = Random.nextInt(60) + 32900.0000f
        val closePrice = Random.nextInt(60) + 32900.0000f
        val maxClose = Math.max(openPrice, closePrice)
        val minClose = Math.min(openPrice, closePrice)
//            val value = mLastEndValue - Random.nextInt(10) - 10
        return KCandleItemBean(
            openPrice,
            closePrice,
            maxClose,
            minClose,
            time = "16:21:21",
            timestamp = ""
        )
    }

    private fun getEndCloseValue(): Float {
        val ccc = Random.nextInt(100)

        val closePrice = if (ccc % 2 == 0) {
            Random.nextInt(15).toFloat()
        } else {
            -Random.nextInt(15).toFloat()
        }
        return closePrice
    }

    companion object {
        fun starts(context: Context) {
            context.startActivity(Intent(context, KCandleActivity::class.java))
        }
    }

}