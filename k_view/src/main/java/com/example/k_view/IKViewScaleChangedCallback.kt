package com.example.k_view


interface IKViewScaleChangedCallback {

    fun onEndValue(value: Float)

    fun onMaxAndMinScale(
        maxValue: Float,
        maxValueInt: Int,
        minValue: Float,
        minValueInt: Int,
        itemCount: Int
    )
}