package com.example.k_view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View


abstract class BaseMeasureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        protected const val DEF_WIDTH = 375
        protected const val DEF_HEIGHT = 667
    }

    init {
        checkAPILevel()
    }


    //测量的控件宽高，会在onMeasure中进行测量。
    protected open var mBaseWidth = 0
    protected open var mBaseHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEF_WIDTH, DEF_HEIGHT)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEF_WIDTH, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, DEF_HEIGHT)
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize)
        }
        mBaseWidth = measuredWidth
        mBaseHeight = measuredHeight
    }

    //API小于18则关闭硬件加速，否则setAntiAlias()方法不生效
    @SuppressLint("ObsoleteSdkInt")
    private fun checkAPILevel() {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(LAYER_TYPE_NONE, null)
        }
    }
}