package com.example.k_view

import kotlin.math.roundToInt

/**
 * 最高、最低价计算辅助类
 * */
fun MutableList<KCandleItemBean>.getMaxAndMinValueV5(
    action: (
        maxValue: Float, maxValueInt: Int,
        minValue: Float, minValueInt: Int, itemCount: Int
    ) -> Unit
) {
    var maxValue = 0f
    var minValue = this[0].highestPrice

    var itemCount = 0

    //找出最大值和最小值
    forEach { kLineChartItemBean ->
//        val value = kLineChartItemBean.value
        if (maxValue < kLineChartItemBean.highestPrice) {
            maxValue = kLineChartItemBean.highestPrice
        }
        if (minValue > kLineChartItemBean.minimumPrice) {
            minValue = kLineChartItemBean.minimumPrice
        }
    }

    var maxValueInt = (maxValue / 10).roundToInt() * 10
    var minValueInt = (minValue / 10).roundToInt() * 10

    //最大和最小值之间差值
    val valueDifference = maxValueInt - minValueInt
    when {
        valueDifference == 90 -> {
            itemCount = 3
        }
        valueDifference == 120 -> {
            itemCount = 4
        }
        valueDifference == 150 -> {
            itemCount = 5
        }
        valueDifference == 160 -> {
            itemCount = 4
        }
        valueDifference <= 200 -> {
            var itemValue = 10
            while (itemValue <= 60) {

                val intArray =
                    obtainItemCount(maxValue, maxValueInt, minValue, minValueInt, itemValue)
                itemCount = intArray[2]
                if (itemCount == 0) {
                    itemValue += 10
                } else {
                    maxValueInt = intArray[0]
                    minValueInt = intArray[1]
                    itemValue = 101
                }
            }

        }
        else -> {
            itemCount = isCanDividerBig(valueDifference)

            while (itemCount == 0) {
                val intArray = changeValueExt(maxValue, maxValueInt, minValue, minValueInt)
                maxValueInt = intArray[0]
                minValueInt = intArray[1]
                itemCount = isCanDividerBig(maxValueInt - minValueInt)
            }
        }
    }

    action.invoke(maxValue, maxValueInt, minValue, minValueInt, itemCount)

}

//获取itemCount
fun obtainItemCount(
    maxValue: Float,
    maxValueInt: Int,
    minValue: Float,
    minValueInt: Int,
    itemValue: Int
): IntArray {
    var minValueChange = minValueInt
    var maxValueChange = maxValueInt

    val valueDifference = maxValueInt - minValueInt
    if (itemValue * 6 >= valueDifference) {
        var itemCount = isCanDividerSmall(maxValueChange - minValueChange, itemValue)

        while (itemCount == 0) {
            val intArray = changeValueExt(maxValue, maxValueChange, minValue, minValueChange)
            maxValueChange = intArray[0]
            minValueChange = intArray[1]
            itemCount = isCanDividerSmall(maxValueChange - minValueChange, itemValue)
        }
        return intArrayOf(maxValueChange, minValueChange, itemCount)
    }
    return intArrayOf(maxValueChange, minValueChange, 0)
}

//是否能被等分（差值较小时使用该方式）
fun isCanDividerSmall(valueDifference: Int, itemValue: Int): Int {
    if (valueDifference % itemValue == 0) {

        val itemCount = valueDifference / itemValue
        if (itemCount in 3..6) {
            return itemCount
        }
    }
    return 0
}

//是否能被等分（差值过大时使用该方式）
fun isCanDividerBig(valueDifference: Int): Int {
    when {
        valueDifference % 30 == 0 -> {//能被3等分
            return 3
        }
        valueDifference % 40 == 0 -> {//能被4等分
            return 4
        }
        valueDifference % 50 == 0 -> {//能被5等分
            return 5
        }
        valueDifference % 60 == 0 -> {//能被6等分
            return 6
        }
        else -> {
            return 0
        }
    }
}

//差值无法等分时，改变最大、最小value值
fun changeValueExt(
    maxValue: Float,
    maxValueInt: Int,
    minValue: Float,
    minValueInt: Int
): IntArray {


    var minValueChange = minValueInt
    var maxValueChange = maxValueInt
    when {
        minValueInt - minValue > 0 -> {//all or min
            minValueChange = minValueInt - 10
        }
        maxValue - maxValueInt > 0 -> {//max
            maxValueChange = maxValueInt + 10
        }
        else -> {//no
            minValueChange = minValueInt - 10
        }
    }

    return intArrayOf(maxValueChange, minValueChange)

}