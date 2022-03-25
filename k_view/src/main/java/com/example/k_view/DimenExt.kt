package com.example.k_view

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * dimen 扩展函数
 */
//---------------------------- context dimen extension ------------------------------------
fun Context.dp2px(dpValue: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        this.resources.displayMetrics
    )
}

fun Context.getScreenWidth(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun Context.getScreenHeight(): Int {
    return this.resources.displayMetrics.heightPixels
}
//---------------------------- context dimen extension end ------------------------------------


//---------------------------- activity dimen extension ------------------------------------
fun AppCompatActivity.dp2px(dpValue: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        this.resources.displayMetrics
    )
}

fun AppCompatActivity.getScreenWidth(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun AppCompatActivity.getScreenHeight(): Int {
    return this.resources.displayMetrics.heightPixels
}
//---------------------------- activity dimen extension end ------------------------------------


//---------------------------- fragment dimen extension ------------------------------------
fun Fragment.dp2px(dpValue: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        this.resources.displayMetrics
    )
}

fun Fragment.getScreenWidth(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun Fragment.getScreenHeight(): Int {
    return this.resources.displayMetrics.heightPixels
}
//---------------------------- fragment dimen extension end  ------------------------------------


//---------------------------- view dimen extension ------------------------------------
fun View.dp2px(dpValue: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        this.resources.displayMetrics
    )
}

fun View.getScreenWidth(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun View.getScreenHeight(): Int {
    return this.resources.displayMetrics.heightPixels
}
//---------------------------- view dimen extension end  ------------------------------------