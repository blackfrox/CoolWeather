package com.example.weather.ui.adapter

import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.weather.R
import com.example.weather.other.db.CityWeather
import com.example.weather.util.ThemeUtil
import com.example.weather.util.parse

class CityManagerAdapter(data: List<CityWeather>, resId: Int = R.layout.item_city_manager)
    : BaseItemDraggableAdapter<CityWeather,BaseViewHolder>(resId,data) {

    override fun convert(helper: BaseViewHolder, item: CityWeather) {
        with(helper){
            with(item){
                if (helper.adapterPosition == 0) {
                    getView<TextView>(R.id.tv_county_name).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ThemeUtil.setTintDrawable(R.drawable.ic_location, mContext,
                                    ThemeUtil.getCurrentColorPrimary(mContext)), null)
                }
                setText(R.id.tv_county_name,countyName)
                setText(R.id.tv_weather,weather)
                setText(R.id.tv_tmp,tmp)
                Glide.with(mContext)
                        .load(parse(weather))
            }
        }
    }
}