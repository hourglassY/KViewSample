package com.example.k_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import kotlin.math.min


abstract class KCandleGridLineDrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : KScrollView(context, attrs) {

    //网格线Paint
    private val mPaintGridLine = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintGridLineColor = Color.parseColor("#1adee0e9")

    //底部文字Paint
    private val mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPaintTextColor = Color.parseColor("#444547")
    private val mBounds = Rect(0, 0, 0, 0)

    //1min处虚线Paint
    private val mPaintDash = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintDashColor = Color.parseColor("#535353")

    //右侧实线
    private val mPaintLineEnd = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintLineEndColor = Color.parseColor("#b3dee0e9")


    //可绘制部分高度（除去底部x轴文字所占高度）
    protected open var mDrawHeight = 0f

    //网格线（水平方向）最大值、最小值
    protected open var mMaxValueInt = 0
    protected open var mMinValueInt = 0

    //实际最大值、最小值
    protected open var mMaxValue = 0f
    protected open var mMinValue = 0f

    //网格线（水平方向）数量
    protected open var mGridLineHorCount = 4

    //网格线距顶部高度
    protected open var mLineTopHeight = 0f

    //网格线距底部高度
    protected open var mLineBottomHeight = 0f

    //折线 实际可绘制高度
    protected open var mLinChartHeight = 0f


    init {
        mPaintGridLine.color = mPaintGridLineColor
        mPaintGridLine.style = Paint.Style.STROKE
        mPaintGridLine.strokeWidth = 1f

        mPaintText.textSize = dp2px(10f)
        mPaintText.color = mPaintTextColor

        mPaintDash.color = mPaintDashColor
        mPaintDash.style = Paint.Style.STROKE
        mPaintDash.strokeWidth = dp2px(1f)
        mPaintDash.pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 10f)

        mPaintLineEnd.color = mPaintLineEndColor
        mPaintLineEnd.style = Paint.Style.STROKE
        mPaintLineEnd.strokeWidth = dp2px(1f)


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawHeight = mBaseHeight - dp2px(32f)
    }


    fun drawOther(canvas: Canvas) {
        getData()?.let {
            drawHorLine(canvas)

            drawGridLineVer(canvas)

            drawDashLine(canvas)

            drawEndLine(canvas)
        }

    }


    //绘制垂直方向网格线及底部文字
    private fun drawGridLineVer(canvas: Canvas) {
        //网格间刻度数量-垂直方向
        val gridLineScale = mScreenScale / mGridLineCountVer
        //要绘制的网格线总数量-垂直方向
        val gridLineCountVerAll = mMaxScale / gridLineScale + 1
        for (pos in 0..gridLineCountVerAll) {
            val posScale = pos * gridLineScale
            val xLine = mInterval * posScale
            canvas.drawLine(
                xLine, mDrawHeight, xLine, 0f, mPaintGridLine
            )
            val time = if (posScale < getData()!!.size) {
                getData()!![posScale].time
            } else {
                "16:21:21"
            }
            drawVerticalText(canvas, time, xLine)
        }
    }


    //绘制底部文字
    private fun drawVerticalText(canvas: Canvas, text: String, xLine: Float) {
        mPaintText.getTextBounds(text, 0, text.length, mBounds)
        val textHeight = mBounds.bottom - mBounds.top
        val textY =
            mDrawHeight + dp2px(32f) / 2 + textHeight / 2 - dp2px(0.5f)
        val textWidth = mPaintText.measureText(text)
        val textX = xLine - textWidth * 0.5f - dp2px(0.5f)
        canvas.drawText(text, textX, textY, mPaintText)
    }

    //绘制右侧1min虚线
    private fun drawDashLine(canvas: Canvas) {
        val xLine = mInterval * (mMaxScale - 1) + dp2px(1f)
        canvas.drawLine(
            xLine, mDrawHeight, xLine, 0f, mPaintDash
        )
    }


    //绘制最右侧实线
    private fun drawEndLine(canvas: Canvas) {
        val endX = mViewLength - dp2px(1f)
        canvas.drawLine(
            endX, mDrawHeight, endX, 0f, mPaintLineEnd
        )
    }


    //绘制网格线-水平方向
    private fun drawHorLine(canvas: Canvas) {


        val gridHeight =
            (mDrawHeight - mLineTopHeight - mLineBottomHeight) / mGridLineHorCount
        val endX = mInterval * mMaxScale + width / 4

        for (pos in 0..mGridLineHorCount) {
            val yLine = gridHeight * pos + mLineTopHeight
            canvas.drawLine(0f, yLine, endX, yLine, mPaintGridLine)
        }


    }

    //滑动监听 currentScale当前可见的第一个刻度值
    override fun onCurrentValueChanged(currentScale: Int) {
        changeHortParams(currentScale)
    }

    //改变水平方向参数，（文字，网格线等）
    protected open fun changeHortParams(currentScale: Int) {
        getData()?.let {
            val toIndex = min(currentScale + mScreenScale + 2, mMaxScale)
            val data = it.subList(currentScale, toIndex)


            data.getMaxAndMinValueV5 { maxValue, maxValueInt, minValue, minValueInt, itemCount ->
                mMaxValue = maxValue
                mMaxValueInt = maxValueInt
                mMinValue = minValue
                mMinValueInt = minValueInt
                mGridLineHorCount = itemCount
            }
            mIkViewScaleCallback?.onMaxAndMinScale(
                mMaxValue,
                mMaxValueInt,
                mMinValue,
                mMinValueInt,
                mGridLineHorCount
            )

            val value10Height = mDrawHeight * 0.84f * 10 / (mMaxValue - mMinValue)
            mLineBottomHeight =
                mDrawHeight * 0.08f + (mMinValueInt - mMinValue) * value10Height / 10
            mLineTopHeight = mDrawHeight * 0.08f + (mMaxValue - mMaxValueInt) * value10Height / 10

            mLineBottomHeight = min(mLineBottomHeight, mDrawHeight / mGridLineHorCount)
            mLineTopHeight = min(mLineTopHeight, mDrawHeight / mGridLineHorCount)
//        mLineTopHeight = if (mMaxValue - mMaxValueInt > 0) {
//            dp2px(40f)
//        } else {
//            dp2px(20f)
//        }
//        mLineBottomHeight = if (mMinValueInt - mMinValue > 0) {
//            dp2px(40f)
//        } else {
//            dp2px(20f)
//        }
            mLinChartHeight = mDrawHeight - mLineTopHeight - mLineBottomHeight

        }

    }

    //获取数据
    abstract fun getData(): MutableList<KCandleItemBean>?

    protected var mIkViewScaleCallback: IKViewScaleChangedCallback? = null
    fun setKViewScaleCallback(ikViewScaleCallback: IKViewScaleChangedCallback) {
        this.mIkViewScaleCallback = ikViewScaleCallback
    }

}