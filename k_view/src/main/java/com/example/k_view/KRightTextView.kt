package com.example.k_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet

/**
 * 绘制右侧Y轴文字
 * */
class KRightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : BaseMeasureView(context, attrs) {

    /********************************* 可配置参数start *********************************/
    //y轴最大值和最小值
//    private var mMaxValueVer = 100
//    private var mMinValueVer = 0
    private var mCurValue = 20f

    //相邻文字之间value差值
    private var mItemValue = 20

    //底部x轴文字所占高度
    private var mBottomHeight = 32f
    private val mBottomHeightDp: Float by lazy { dp2px(mBottomHeight) }

    //y轴文字TextColor
    private var mTextColor = Color.parseColor("#444547")

    //当前value值文字color
    private var mTextCurColor = Color.parseColor("#ffffff")

    //圆角矩形color
    private var mRectColor = Color.parseColor("#6183fe")
    /********************************* 可配置参数End *********************************/

    /********************************* View内部使用参数Start *********************************/
    //可绘制部分高度（除去底部x轴文字所占高度）
    private var mDrawHeight = 0f
    private val mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBounds = Rect(0, 0, 0, 0)

    private val mPaintTextCur = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintRect = Paint(Paint.ANTI_ALIAS_FLAG)

    //网格线（水平方向）最大值、最小值
    private var mMaxValueInt = 0
    private var mMinValueInt = 0

    //实际最大值、最小值
    private var mMaxValue = 0f
    private var mMinValue = 0f

    //网格线（水平方向）数量
    private var mGridLineHorCount = 4

    //网格线距顶部高度
    private var mLineTopHeight = 0f

    //网格线距底部高度
    private var mLineBottomHeight = 0f

    //折线 实际可绘制高度
    private var mLinChartHeight = 0f

    /********************************* View内部使用参数End *********************************/

    init {
        mPaintText.color = mTextColor
        mPaintText.textSize = dp2px(10f)

        mPaintTextCur.color = mTextCurColor
        mPaintTextCur.textSize = dp2px(12f)

        mPaintRect.color = mRectColor
        mPaintRect.textSize = dp2px(12f)
        mPaintRect.style = Paint.Style.FILL

        setBackgroundColor(Color.parseColor("#141517"))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawHeight = mBaseHeight - mBottomHeightDp
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawTextVertical(canvas)

        drawTextCur(canvas)
    }

    /**
     * 绘制Y轴文字
     * */
    private fun drawTextVertical(canvas: Canvas) {
        val gridHeight = (mDrawHeight - mLineTopHeight - mLineBottomHeight) / mGridLineHorCount
        val endX = mBaseWidth - dp2px(2f)

        for (pos in 0..mGridLineHorCount) {
            val yLine = gridHeight * pos + mLineTopHeight
            val text = (mMaxValueInt - mItemValue * pos).toString()

            mPaintText.getTextBounds(text, 0, text.length, mBounds)
            val textHeight = mBounds.bottom - mBounds.top
            val textY = yLine + textHeight / 2 - dp2px(0.5f)
            val textWidth = mPaintText.measureText(text)
            val startX = endX - textWidth
//            Log.d("tag_line_chart", "drawVerticalText: startX = $startX, textY = $textY")
            canvas.drawText(text, startX, textY, mPaintText)
        }

    }

    /**
     * 绘制背景为圆角矩形的文字（当前时间对应的value值）
     * */
    private fun drawTextCur(canvas: Canvas) {
        val ratio = (mCurValue - mMinValueInt) / (mMaxValueInt - mMinValueInt)
        val yLine = (1 - ratio) * mLinChartHeight + mLineTopHeight
//        val yLine =
//            mDrawHeight - (mCurValue - mMinValueInt) / (mMaxValueInt - mMinValueInt) * mDrawHeight
        val rectTop = yLine - dp2px(9f)
        val rectBottom = yLine + dp2px(9f)
        canvas.drawRoundRect(0f, rectTop, mBaseWidth.toFloat(), rectBottom, 500f, 500f, mPaintRect)

        val text = mCurValue.toString()
        mPaintTextCur.getTextBounds(text, 0, text.length, mBounds)
        val textHeight = mBounds.bottom - mBounds.top
        val textY = yLine + textHeight / 2 - dp2px(0.5f)
        val textWidth = mPaintText.measureText(text)
        val startX = (mBaseWidth - textWidth) / 2
//        Log.d("tag_line_chart", "drawVerticalText: startX = $startX, textY = $textY")
        canvas.drawText(text, startX, textY, mPaintTextCur)

    }


    fun setMaxAndMinValue(
        maxValue: Float,
        maxValueInt: Int,
        minValue: Float,
        minValueInt: Int,
        itemCount: Int
    ) {
        mMaxValue = maxValue
        mMaxValueInt = maxValueInt
        mMinValue = minValue
        mMinValueInt = minValueInt
        mGridLineHorCount = itemCount
        mItemValue = (mMaxValueInt - mMinValueInt) / mGridLineHorCount

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
//        mLinChartHeight = mDrawHeight - mLineTopHeight - mLineBottomHeight

        val value10Height = mDrawHeight * 0.84f * 10 / (mMaxValue - mMinValue)
        mLineBottomHeight = mDrawHeight * 0.08f + (mMinValueInt - mMinValue) * value10Height / 10
        mLineTopHeight = mDrawHeight * 0.08f + (mMaxValue - mMaxValueInt) * value10Height / 10
        mLinChartHeight = mDrawHeight - mLineTopHeight - mLineBottomHeight
        invalidate()
    }

    fun setCurrentValue(curValue: Float) {
        mCurValue = curValue
        invalidate()
    }
}