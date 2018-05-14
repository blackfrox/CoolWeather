package com.example.coolweather.base

interface BaseView<T> {
     var presenter: T  //presenter的赋值写在实例Presenter的init块中{}
}