package com.example.k_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet

class KCandleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : KCandleGridLineDrawView(context, attrs) {

    private val mPaintRect = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mColorRed = Color.parseColor("#ff6766")
    private val mColorGreen = Color.parseColor("#26b276")
    private val mColorEqual = Color.parseColor("#12ec3a")

    private val mPaintDynamicLine = Paint(Paint.ANTI_ALIAS_FLAG)


    init {
        mScreenScale = 40
        mPaintRect.style = Paint.Style.FILL
        mPaintRect.strokeWidth = dp2px(1f)

        mPaintDynamicLine.color = Color.parseColor("#626365")
        mPaintDynamicLine.style = Paint.Style.FILL
        mPaintDynamicLine.strokeWidth = dp2px(1f)

        setBackgroundColor(Color.parseColor("#141517"))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mData?.let {
            drawCandleRect(canvas)
            drawOther(canvas)

            drawEndCircle(canvas)
        }

    }

    private fun drawCandleRect(canvas: Canvas) {
        mData!!.forEachIndexed { index, bean ->


            val leftF: Float = (index - 1) * mInterval + 1
            val rightF: Float = index * mInterval - 1
            val topF: Float = getYPx(bean.closePrice.coerceAtLeast(bean.openPrice))
            var bottomF: Float = getYPx(bean.closePrice.coerceAtMost(bean.openPrice))

            val renderColor = when {
                bean.closePrice > bean.openPrice -> {
                    mColorGreen
                }
                bean.closePrice < bean.openPrice -> {
                    mColorRed
                }
                else -> {
                    //上下边界一样，设置一个偏移值
                    if (topF == bottomF) bottomF += dp2px(1f)
                    mColorEqual
                }
            }
            mPaintRect.color = renderColor

            canvas.drawRect(leftF, topF, rightF, bottomF, mPaintRect)

            val xLine = leftF + (rightF - leftF) / 2
            val yStart = getYPx(bean.highestPrice)
            val yEnd = getYPx(bean.minimumPrice)
            canvas.drawLine(xLine, yStart, xLine, yEnd, mPaintRect)
        }
    }

    private fun drawEndCircle(canvas: Canvas) {

        val cX = mInterval * (mMaxScale - 1) + dp2px(1f)
        val endValue = mData!![mData!!.size - 1].closePrice
        val yLineTo = getYPx(endValue)
//        val cY =
//            mDrawHeight - (endValue - mMinValueInt) / (mMaxValueInt - mMinValueInt) * mDrawHeight


        val xLineEnd = cX + width / mGridLineCountVer
        canvas.drawLine(0f, yLineTo, xLineEnd, yLineTo, mPaintDynamicLine)

        mIkViewScaleCallback?.onEndValue(endValue)
    }

    private fun getRatio(value: Float) = (value - mMinValueInt) / (mMaxValueInt - mMinValueInt)

    private fun getYPx(value: Float) = (1 - getRatio(value)) * mLinChartHeight + mLineTopHeight

    override fun getData() = mData

    private var mData: MutableList<KCandleItemBean>? = null


    fun setData(data: MutableList<KCandleItemBean>) {

        mData = data
        mMaxScale = mData?.size ?: 0
        onRefreshSize()
        invalidate()
        goToScale(mMaxScale.toFloat())
    }

    fun addData(bean: KCandleItemBean) {

//        mData?.add(bean)
//        mMaxScale = mData?.size ?: 0
//        onRefreshSize()
//
//        invalidate()
//        goToScaleEnd()
        if (mMaxScale >= 2000) {
            mData?.removeAt(0)
        }
        mData?.add(bean)
        mMaxScale = mData?.size ?: 0
        onRefreshSize()

        invalidate()
        goToScaleEnd()

    }

    fun changeEndData(bean: KCandleItemBean) {
        mData?.let {
            it.set(it.size - 1, bean)
            mMaxScale = mData?.size ?: 0
            onRefreshSize()

            invalidate()
            goToScaleEnd()
        }

    }

    fun changeEndDataCloseAdd(value: Float) {
        mData?.let {
            val beanEnd = it.get(it.size - 1)
            beanEnd.closePrice += value
            beanEnd.minimumPrice = Math.min(beanEnd.closePrice, beanEnd.minimumPrice)
            beanEnd.highestPrice = Math.max(beanEnd.closePrice, beanEnd.highestPrice)

            //当前刻度，距最后一个刻度的距离
            val fromEndScale = mMaxScale - mCurrentScale

            //如果最后一个刻度，在屏幕上可见，则让他改变水平方向参数
            if (fromEndScale < mScreenScale + 10) {
                changeHortParams(mCurrentScale.toInt())
            }

            invalidate()
//        goToScaleEnd()
        }

    }

    fun changeEndDataCloseCut(value: Float) {
        mData?.let {
            val beanEnd = it.get(it.size - 1)
            beanEnd.closePrice -= value
            beanEnd.minimumPrice = Math.min(beanEnd.closePrice, beanEnd.minimumPrice)
            beanEnd.highestPrice = Math.max(beanEnd.closePrice, beanEnd.highestPrice)


            //当前刻度，距最后一个刻度的距离
            val fromEndScale = mMaxScale - mCurrentScale

            //如果最后一个刻度，在屏幕上可见，则让他改变水平方向参数
            if (fromEndScale < mScreenScale + 10) {
                changeHortParams(mCurrentScale.toInt())
            }



            invalidate()
//        goToScaleEnd()
        }

    }


}