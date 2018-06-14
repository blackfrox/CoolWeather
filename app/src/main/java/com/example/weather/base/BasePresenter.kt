package com.example.weather.base

import com.baidu.location.BDLocation
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Java8 新增的特性：允许在接口中实现默认方法和使用静态常量
 */
interface BasePresenter {
    companion object {
        val compositeDisposable by lazy { CompositeDisposable() }
    }

    fun start() { }
    fun start(bdLocation: BDLocation) {}

    fun addSubscribe(@NonNull vararg ds: Disposable) {
        compositeDisposable.addAll(*ds)
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }
}