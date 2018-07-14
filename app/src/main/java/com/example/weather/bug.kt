package com.example.weather

/**
 * //TODO: bug 右上角的分享按钮点击无效
 *
 *
 * 后期优化:　１　城市管理界面自己写一个
 * 　　　　　　２　天气界面改版
 * 　　　　　
 * 已解决:
 * 1 数据库添加了，但是MainActivity没有更新
 * 2 撤销后，数据库并没有添加
 * 3 冷启动时间有点久  (代码混淆)
 * 4 不知道为什么kotlin-extension的view有时候是红色没有加载，不知道是这个项目的未知错误，还是kotlin本身的问题(现在没事了)
 * 小知识：
 * 1 使用?attr/colorPrimary 代替@color/colorPrimary，可以方便后期增加主题切换功能
 *
 */
