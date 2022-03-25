package com.example.k_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

/**
 * 绘制折线图
 * */
class KLinChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : KCandleGridLineDrawView(context, attrs) {

    private val mPaintLine = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPathLine = Path()
    private var mPaintLineColor = Color.parseColor("#6183fe")
    private var mPaintLineWidth = 4.0f

    private val mPaintBg = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPathBg = Path()
    private var mPaintBgColor = Color.parseColor("#74caf9")
    private var mBgStartColor = Color.parseColor("#5678e2")
    private var mBgEndColor = Color.parseColor("#141b21")
    private val shadeColors: IntArray = intArrayOf(
        Color.parseColor("#5678e2"),
        Color.parseColor("#313f6f"),
        Color.parseColor("#141b21")
    )


    private val mPaintDynamicLine = Paint(Paint.ANTI_ALIAS_FLAG)


    private val mPaintCircleSmall = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintCircleBig = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintCircleSmallColor = Color.parseColor("#ffffff")


    private var mShader: Shader? = null

    init {
        mPaintLine.color = mPaintLineColor
        mPaintLine.strokeWidth = dp2px(1.5f)
        mPaintLine.style = Paint.Style.STROKE
//        mPaintLine.strokeCap = Paint.Cap.ROUND
        mPaintLine.strokeJoin = Paint.Join.ROUND
        mPaintLine.strokeMiter = 1f

        mPaintBg.color = mPaintBgColor
        mPaintBg.strokeWidth = dp2px(1f)
        mPaintBg.style = Paint.Style.FILL
        mPaintBg.strokeJoin = Paint.Join.ROUND
        mPaintBg.strokeMiter = 1f


        mPaintDynamicLine.color = Color.parseColor("#626365")
        mPaintDynamicLine.style = Paint.Style.FILL
        mPaintDynamicLine.strokeWidth = dp2px(1f)

        mPaintCircleSmall.color = mPaintCircleSmallColor
        mPaintCircleSmall.style = Paint.Style.FILL

        mPaintCircleBig.color = mPaintLineColor
        mPaintCircleBig.style = Paint.Style.FILL

        setBackgroundColor(Color.parseColor("#141517"))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mShader = LinearGradient(
            0f, 0f, 0f, mDrawHeight,
            mBgStartColor, mBgEndColor, Shader.TileMode.CLAMP
        )
        mPaintBg.shader = mShader
    }


    override fun getData(): MutableList<KCandleItemBean>? {
        return mData
    }


    override fun invalidate() {
        mPathBg.reset()
        mPathLine.reset()
        super.invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mData?.let {
            drawLineChart(canvas)
            drawOther(canvas)
            drawEndCircle(canvas)
        }

    }


    private fun drawLineChart(canvas: Canvas) {
        mPathLine.moveTo(0f, mDrawHeight)
        mPathBg.moveTo(0f, mDrawHeight)


        for ((pos, bean) in mData!!.withIndex()) {
            val xLineTo = mInterval * pos
//            val ratio = (bean.value - mMinValueInt) / (mMaxValueInt - mMinValueInt)
            val yLineTo = getYPx(bean.highestPrice)
//            logD("KView","drawLineChart pos = $pos, ratio = $ratio, yLineTo = $yLineTo, value = ${bean.value}, mMinValueInt = $mMinValueInt, mMaxValueInt= $mMaxValueInt")
            mPathLine.lineTo(xLineTo, yLineTo)
            mPathBg.lineTo(xLineTo, yLineTo)

        }

        val xLineToEnd = mInterval * (mMaxScale - 1)
        mPathBg.lineTo(xLineToEnd, mDrawHeight)
        mPathBg.close()

        mPaintLine.pathEffect = CornerPathEffect(20f)
        mPaintBg.pathEffect = CornerPathEffect(20f)

        canvas.drawPath(mPathBg, mPaintBg)
        canvas.drawPath(mPathLine, mPaintLine)

    }


    private fun drawEndCircle(canvas: Canvas) {

        val cX = mInterval * (mMaxScale - 1) + dp2px(1f)
        val endValue = mData!![mData!!.size - 1].highestPrice
        val ratio = (endValue - mMinValueInt) / (mMaxValueInt - mMinValueInt)
        val yLineTo = getYPx(endValue)
//        val cY =
//            mDrawHeight - (endValue - mMinValueInt) / (mMaxValueInt - mMinValueInt) * mDrawHeight


        val xLineEnd = cX + width / mGridLineCountVer
        canvas.drawLine(0f, yLineTo, xLineEnd, yLineTo, mPaintDynamicLine)

        canvas.drawCircle(cX, yLineTo, dp2px(6f), mPaintCircleBig)
        canvas.drawCircle(cX, yLineTo, dp2px(2f), mPaintCircleSmall)

        mIkViewScaleCallback?.onEndValue(endValue)
    }

    private fun getRatio(value: Float) = (value - mMinValueInt) / (mMaxValueInt - mMinValueInt)

    private fun getYPx(value: Float) = (1 - getRatio(value)) * mLinChartHeight + mLineTopHeight

    private var mData: MutableList<KCandleItemBean>? = null

    fun setData(data: MutableList<KCandleItemBean>) {

        mData = data
        mMaxScale = mData?.size ?: 0
        onRefreshSize()
        invalidate()
        goToScale(mMaxScale.toFloat())
    }

    fun addData(bean: KCandleItemBean) {
        mPathBg.reset()
        mPathLine.reset()

        if (mMaxScale >= 2000) {
            mData?.removeAt(0)
        }
        mData?.add(bean)
        mMaxScale = mData?.size ?: 0
        onRefreshSize()

        invalidate()
        goToScaleEnd()


    }


    fun delAndAddData(bean: KCandleItemBean) {
        mPathBg.reset()
        mPathLine.reset()

        mData?.removeAt(0)
        mData?.add(bean)
        mMaxScale = mData?.size ?: 0
        onRefreshSize()
        invalidate()
    }


}