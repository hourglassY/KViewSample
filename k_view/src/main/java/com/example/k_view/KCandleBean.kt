package com.example.k_view


data class KCandleBean(val data: MutableList<KCandleItemBean> = mutableListOf())
data class KCandleItemBean(
    //如果是1s走势图，开盘价、收盘价、最高价、最低价需相同
    var openPrice: Float = 0f,//开盘价
    var closePrice: Float = 0f,//收盘价
    var highestPrice: Float = 0f,//最高价
    var minimumPrice: Float = 0f,//最低价
    var statusGuess: Int = 0,//竞猜状态 1=无 2=call 3=put 4=已结算
    var guessAmount: Float = 0f,//竞猜金额
    val time: String = "",//时间
    val timestamp: String = ""//时间戳
)
