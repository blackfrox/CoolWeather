package com.example.weather.base

interface BaseView<T> {
     var presenter: T //presenter的赋值写在BasePresenter子类的init块中{}
}