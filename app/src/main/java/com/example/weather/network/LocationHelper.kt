package com.example.weather.network

import com.baidu.location.*
import com.example.weather.MyApp

/**
 *
 */
class LocationHelper {
    companion object {
        val instance by lazy { LocationHelper() }
    }

    private val mLocationClient: LocationClient = LocationClient(MyApp.instance.applicationContext)
            .apply {
                locOption = LocationClientOption()
                        .apply {
                            setIsNeedAddress(true)
                        }
            }

    fun locate(action:(BDLocation)-> Unit) {
        mLocationClient.apply {
            registerLocationListener(object : BDAbstractLocationListener(){
                override fun onReceiveLocation(p0: BDLocation) {
                    action(p0)
                    //防止内存泄露
                    unRegisterLocationListener(this)
                    stop()
                }
            })
            start()
        }
    }


}