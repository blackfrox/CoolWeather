package com.example.wanandroidtest.util

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Administrator on 2018/4/22 0022.
 */

/**
 * 统一线程处理
 * @param <T> 指定的泛型类型
 * @return ObservableTransformer
</T> */
fun <T>Observable<T>.applyScheduler(): Observable<T>{
    return subscribeOn(Schedulers.io())
//            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}
