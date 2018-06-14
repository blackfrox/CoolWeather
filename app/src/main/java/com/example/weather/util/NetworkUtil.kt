package com.example.weather.util

import android.content.Context
import android.net.ConnectivityManager
import com.example.weather.MyApp

/**
 * Created by Administrator on 2017/8/15.
 */
object NetworkUtil{

    fun isNetConnected(context: Context= MyApp.instance): Boolean{
        val connectManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectManager.activeNetworkInfo
        if (networkInfo==null){
            return false
        }else{
            return networkInfo.isAvailable&& networkInfo.isConnected
        }
    }

    fun isNetworkConnected(context: Context,typeMoblie: Int): Boolean{
        if (!isNetConnected(context)){
            return false
        }
        val connectManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectManager.getNetworkInfo(typeMoblie)
        if (networkInfo==null){
            return false
        }else{
            return networkInfo.isConnected&&networkInfo.isAvailable
        }
    }



    fun isPhoneNetConnected(context: Context): Boolean{
       val typeMobile=ConnectivityManager.TYPE_MOBILE
       return isNetworkConnected(context,typeMobile)
   }



    fun isWifiNetConnected(context: Context): Boolean{
        val typeMobile=ConnectivityManager.TYPE_WIFI
        return isNetworkConnected(context,typeMobile)
    }



}