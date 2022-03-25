package com.example.kviewsample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

suspend inline fun flowLooper(
    crossinline onStart: () -> Unit,
    crossinline onCollect: () -> Unit,
) {
    flow {

        while (true) {
            emit(0)
            delay(1000)
        }
    }.flowOn(Dispatchers.Main)
        .onStart {
//            logD("onStart")
            onStart.invoke()
        }
//        .onCompletion { onFinish?.invoke() }
//        .onEach {}
        .collect {
            onCollect.invoke()
//            logD("collect $it")
        }

}