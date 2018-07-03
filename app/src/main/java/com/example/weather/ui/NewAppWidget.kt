package com.example.weather.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.weather.R
import com.example.weather.other.db.CityWeather
import com.example.weather.ui.main.MainActivity
import com.example.weather.util.parse
import org.litepal.crud.DataSupport
import android.R.attr.button
import android.app.PendingIntent




/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        //每次窗口小部件被更新都调用一次该方法
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }


    }

    /**
     * 接受窗口小部件点击时发送的广播
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    /**
     * 每次删除是调用该方法
     */
    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }
    //当该窗口小部件第一次被添加时调用
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    //当最后一个该窗口小部件删除时调用
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)

           DataSupport.findFirst(CityWeather::class.java)
                   .apply {
                       views.apply {
                           setImageViewResource(R.id.img_weather, parse(weather))
                           setTextViewText(R.id.tv_weather,weather)
                           setTextViewText(R.id.tv_tmp,"$tmp℃")
                           setTextViewText(R.id.tv_county_name,countyName)
                       }
                   }

            // Create an Intent to launch ExampleActivity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

