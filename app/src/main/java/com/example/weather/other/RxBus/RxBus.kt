package com.example.weather.other.RxBus

import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import io.reactivex.subscribers.SerializedSubscriber

/**
 * 1 介绍: 使用RxJava2编写的EventBus。
 *
 * 2 作用：代替广播 进行组件之间的通信
 *
 *３使用步骤：①上级注册接收使用: toFlowable()，②下级发送使用:　post(**::class.java)
 *
 */
class RxBus {

    //调用toSerialized(),保证线程安全
    private val mBus=PublishProcessor.create<Any>().toSerialized()

    /**
     * 发送消息
     */
    fun post(o: Any)=SerializedSubscriber(mBus).onNext(o)

    /**
     * 确定接收消息的类型
     */
    fun <T>toFlowable(aClass: Class<T>): Flowable<T> {
        return mBus.ofType(aClass)
    }

    /**
     * 判断是否有订阅
     */
    fun hasSubscribers()=
            mBus.hasSubscribers()

    companion object {
        //注: 下面的变量和方法的意思是一样的，就看你喜欢用哪个
        val instance by lazy { RxBus() }
        fun getDefault() = instance
    }

}
