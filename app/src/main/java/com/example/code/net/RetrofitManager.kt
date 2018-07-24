package com.example.code.net

import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

object RetrofitManager {
    lateinit var retrofit: Retrofit // 该demo为了演示插件的作用，没有进行初始化
    fun <T> threadSwitch(): Observable.Transformer<T, T> {
        return Observable.Transformer { observable ->
            observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}