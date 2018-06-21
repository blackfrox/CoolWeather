package com.example.weather.base

import com.baidu.location.BDLocation
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Java8 新增的特性：允许在接口中实现默认方法和使用静态常量
 *
 * 介绍:通过compositeDisposable统一管理observable(RxJava2有关)
 * 作用: 避免内存泄露
 */
interface BasePresenter {
    companion object {
        val compositeDisposable by lazy { CompositeDisposable() }
    }

    fun start() { }

    fun addSubscribe(@NonNull vararg ds: Disposable) {
        compositeDisposable.addAll(*ds)
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }
}