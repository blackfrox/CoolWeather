package com.example.weather.base

/**
 * 使用的是官方 todo-mvp-kotlin 的MVP模板
 * @link https://github.com/googlesamples/android-architecture/tree/todo-mvp-kotlin/
 * mvp作用: 模块解耦，方便修改、测试之类的
 *
 * 例子:1 编写一个contract接口(内置view和presenter的接口)
 *      2 写一个presenter子类(继承contract.presenter)
 *      3 将需要的activity/fragment实现contract.view功能
 */
interface BaseView<T> {
     var presenter: T //presenter的赋值写在BasePresenter子类的init块中{}
}