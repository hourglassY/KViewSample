package com.example.k_view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.OverScroller
import androidx.annotation.Px
import kotlin.math.abs
import kotlin.math.roundToInt


abstract class KScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : BaseMeasureView(context, attrs) {

    companion object {
        //加入放大倍数来防止精度丢失而导致无限绘制
        private const val SCALE_TO_PX_FACTOR = 100

        //惯性回滚最小偏移值，小于这个值就应该直接滑动到目的点
        private const val MIN_SCROLLER_DP = 1f
    }


    //当前刻度值改变时回调
    protected abstract fun onCurrentValueChanged(currentScale: Int)

    /******************************* 可配置参数Start ****************************/
    //获取最大刻度值
    protected open var mMaxScale = 100

    //获取最小刻度值
    protected open var mMinScale = 0

    //屏幕可见刻度数量
    protected open var mScreenScale = 120

    //屏幕可见的 垂直方向网格线数量
    protected open var mGridLineCountVer = 4
    /******************************* 可配置参数Start ****************************/

    /******************************* 内部及子类使用参数Start ****************************/
    protected open var mAllScale = 0
    protected open var mScrollLength = 0f
    protected open var mMaxPosition = 0
    protected open var mViewLength = 0f

    //当前刻度值
    protected open var mCurrentScale = 0f

    //刻度间隔 px
    protected open var mInterval = 0.0f
    /******************************* 内部及子类使用参数Start ****************************/

    /******************************* 内部使用参数Start ****************************/
    //控制滑动
    private var mOverScroller: OverScroller = OverScroller(context)

    //速度获取
    private var mVelocityTracker: VelocityTracker? = null

    //惯性最大最小速度
    private var mMaximumVelocity = 0
    private var mMinimumVelocity = 0

    //记录上次触摸屏幕的x点坐标
    private var mLastX = 0f

    //最小可滑动值、最大可滑动值
    private var mMinPosition = 0


    /******************************* 内部使用参数End ****************************/


    init {

        //配置速度
        mVelocityTracker = VelocityTracker.obtain()
        mMaximumVelocity = ViewConfiguration.get(context)
            .scaledMaximumFlingVelocity
        mMinimumVelocity = ViewConfiguration.get(context)
            .scaledMinimumFlingVelocity


        //第一次进入，跳转到设定刻度
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                goToScale(mMaxScale.toFloat())
            }
        })

    }

    //处理滑动，主要是触摸的时候通过计算现在的event坐标和上一个的位移量来决定scrollBy()的多少
    //滑动完之后计算速度是否满足Fling，满足则使用OverScroller来计算Fling滑动
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!checkAllowScroll()) {
            return super.onTouchEvent(event)
        }
        val currentX = event.x
        //开始速度检测
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(event)
        val parent = parent as ViewGroup //为了解决刻度尺在scrollview这种布局里面滑动冲突问题
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!mOverScroller.isFinished) {
                    mOverScroller.abortAnimation()
                }
                mLastX = currentX
                parent.requestDisallowInterceptTouchEvent(true) //按下时开始让父控件不要处理任何touch事件
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX: Float = mLastX - currentX
                mLastX = currentX
                scrollBy(moveX.toInt(), 0)
            }
            MotionEvent.ACTION_UP -> {
                //处理松手后的Fling
                mVelocityTracker?.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val velocityX = mVelocityTracker?.xVelocity?.toInt() ?: 0
                if (abs(velocityX) > mMinimumVelocity) {
                    fling(-velocityX)
                } else {
                    scrollBackToCurrentScale()
                }
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
                parent.requestDisallowInterceptTouchEvent(false) //up或者cancel的时候恢复
            }
            MotionEvent.ACTION_CANCEL -> {
                if (!mOverScroller.isFinished) {
                    mOverScroller.abortAnimation()
                }
                //回滚到整点刻度
                scrollBackToCurrentScale()
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
//                releaseEdgeEffects()
                parent.requestDisallowInterceptTouchEvent(false) //up或者cancel的时候恢复
            }
        }
        return true
    }

    //重写滑动方法，设置到边界的时候不滑,并显示边缘效果。滑动完输出刻度。
    override fun scrollTo(@Px x: Int, @Px y: Int) {
        if (!checkAllowScroll()) {
            super.scrollTo(x, y)
        }
        var xScroll = x
        if (xScroll < mMinPosition) {
            xScroll = mMinPosition
        }
        if (xScroll > mMaxPosition) {
            xScroll = mMaxPosition
        }

        if (xScroll != scrollX) {
            super.scrollTo(xScroll, y)
        }
        mCurrentScale = scrollXtoScale(xScroll)
        onCurrentValueChanged(mCurrentScale.toInt())
    }


    override fun computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.currX, mOverScroller.currY)
            //这是最后OverScroller的最后一次滑动，如果这次滑动完了mCurrentScale不是整数，则把尺子移动到最近的整数位置
            if (!mOverScroller.computeScrollOffset()) {
                val currentIntScale = mCurrentScale
                if (abs(mCurrentScale - currentIntScale) > 0.001f) {
                    //Fling完进行一次检测回滚
                    scrollBackToCurrentScale(currentIntScale)
                }
            }
//            postInvalidate()
        }
    }

    private fun fling(vX: Int) {
        mOverScroller.fling(
            scrollX, 0, vX, 0,
            mMinPosition, mMaxPosition, 0, 0
        )
//        invalidate()
    }


    private fun scrollBackToCurrentScale() {
        scrollBackToCurrentScale(mCurrentScale)
    }

    protected open fun scrollBackToCurrentScale(currentIntScale: Float) {
        val intScrollX: Float = scaleToScrollFloatX(currentIntScale)
        val dx = ((intScrollX - SCALE_TO_PX_FACTOR * scrollX) / SCALE_TO_PX_FACTOR).roundToInt()
        if (dx > MIN_SCROLLER_DP) {
            //渐变回弹
            mOverScroller.startScroll(scrollX, scrollY, dx, 0, 500)
            invalidate()
        } else {
            //立刻回弹
            scrollBy(dx, 0)
        }
    }

    //获取控件宽高，设置相应信息
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mInterval = mBaseWidth * 1.0f / mScreenScale
        onRefreshSize()
    }

    protected open fun onRefreshSize() {
        mAllScale = mMaxScale - mMinScale
        mScrollLength = mAllScale * mInterval
        mMaxPosition =
            (mScrollLength - mBaseWidth * 1.0f / mGridLineCountVer * (mGridLineCountVer - 1)).toInt()
        mViewLength = mScrollLength + mBaseWidth * 1.0f / mGridLineCountVer
    }


    //直接跳转到当前刻度
    fun goToScale(scale: Float) {
        if (checkAllowScroll()) {
            scrollTo(scaleToScrollX(scale).toInt(), 0)
        }
    }

    fun goToScaleEnd() {
        if (checkAllowScroll()) {
            //当前刻度，距最后一个刻度的距离
            val fromEndScale = mMaxScale - mCurrentScale

            //如果最后一个刻度，在屏幕上可见，则让他滑动到最后
            if (fromEndScale < mScreenScale + 10) {
                mCurrentScale = mMaxScale.toFloat()
                scrollTo(scaleToScrollX(mCurrentScale).toInt(), 0)
            }
        }
    }

    protected open fun checkAllowScroll(): Boolean {
        //刻度值大于allowScrollScale才允许滑动
        val allowScrollScale = (mScreenScale / mGridLineCountVer) * (mGridLineCountVer - 1)
        return mMaxScale > allowScrollScale
    }

    //把滑动偏移量scrollX转化为刻度Scale
    private fun scrollXtoScale(scrollX: Int): Float {
        //实际滑动距离，滑动偏移量-最小可滑动值
        val actualScrollX = scrollX * 1.0f - mMinPosition
        //滑动的刻度值
        val scrollScale = actualScrollX / mScrollLength * mAllScale
        //加上最小刻度值
        return scrollScale + mMinScale
    }

    //把Scale转化为ScrollX
    private fun scaleToScrollX(scale: Float): Float {
        //刻度值
        val actualScale = scale - mMinScale
        //滑动偏移量
        val scrollX = actualScale / mAllScale * mScrollLength
        return scrollX + mMinPosition
    }

    //把Scale转化为ScrollX,放大SCALE_TO_PX_FACTOR倍，以免精度丢失问题
    private fun scaleToScrollFloatX(scale: Float): Float {
        return (scale - mMinScale) / mAllScale * mScrollLength * SCALE_TO_PX_FACTOR + mMinPosition * SCALE_TO_PX_FACTOR
    }

    fun setConfigVer(gridLineCountVer: Int, screenScale: Int) {
        mGridLineCountVer = gridLineCountVer
        mScreenScale = screenScale
    }


}