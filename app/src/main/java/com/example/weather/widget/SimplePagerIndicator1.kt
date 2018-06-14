package com.example.weather.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import com.example.weather.util.SizeUtils

class SimplePagerIndicator1
@JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attributeSet, defStyleAttr), ViewPager.OnPageChangeListener {

    private var roundPadding = 10
    //从pagerListener中获取以下两个参数
    private var selectedPosition = 0 //从0开始
    private var positionOffset = 0.0f

    private val titles = arrayListOf<String>()
    private var textSize: Int
    private var roundRadius: Float
    private var roundPaint: Paint
    private var selectedRoundPaint: Paint
    private var textPaint: Paint

    //初始化titles集合，textSize，roundRadius，
    //和三个Paint
    init {
        textSize = SizeUtils.sp2px(context, 18F)
        roundRadius = 5f

        roundPaint = Paint()
                .apply {
                    style = Paint.Style.FILL_AND_STROKE
                    strokeWidth = 2f
                    color = Color.WHITE
                    isAntiAlias = true
                    alpha = 100
                }
        selectedRoundPaint = Paint()
                .apply {
                    style = Paint.Style.FILL_AND_STROKE
                    strokeWidth = 2f
                    color = Color.WHITE
                    isAntiAlias = true
                }
        textPaint = Paint()
                .apply {
                    strokeWidth = 0f
                    isAntiAlias = true
                    color = Color.WHITE
                    textSize = textSize
                    textAlign = Paint.Align.CENTER
                }
    }

    @SuppressLint("SwitchIntDef")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val desiredWidth=SizeUtils.dp2px(context,100f)
        val desiredHeight=SizeUtils.dp2px(context,48f)

        val widthMode=MeasureSpec.getMode(widthMeasureSpec)
        val widthSize=MeasureSpec.getSize(widthMeasureSpec)
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)
        val heightSize=MeasureSpec.getSize(heightMeasureSpec)

        //Measure Width
       val width = when(widthMode){
            MeasureSpec.EXACTLY -> { //Must be this size
                widthSize
            }
            MeasureSpec.AT_MOST -> {//Can't be bigger than ..
                Math.min(desiredWidth,widthSize)
            }
            else ->{//Be whatever you whant
                desiredWidth
            }
        }

        //Measure Height
       val height = when(heightMode){
            MeasureSpec.EXACTLY -> { //Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> {//Can't be bigger than ..
                Math.min(desiredHeight,heightSize)
            }
            else ->{//Be whatever you whant
                desiredHeight
            }
        }
        //must call this fun
        setMeasuredDimension(width,height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val count = titles.size
        if (count == 0)
            return
        //baseLine : Y坐标
        //fontMetrics.top: 文字绘制的顶线，文字是不会超过这个顶线的
        //fontMetrics.bottom: .......底线，。。。。。。。。。底线。。
       val textBaseLine=(height-textPaint.fontMetrics.top-textPaint.fontMetrics.bottom)/2
       val roundBaseline = textBaseLine + 20 + roundRadius
        //从第一个圆点到最后一个圆点的总长度(n个圆的直径+ (n-1)个roundPadding)
        val roundTotalWidth = 2 * roundRadius * count +  roundPadding* (count - 1)
        //第一个圆点的圆心的X坐标
        val startX = width / 2 - roundTotalWidth / 2 + roundRadius
        //绘制count个半透明圆点，间距为roundPadding
        for (i in 0 until count)
            canvas?.drawCircle(startX + 2 * roundRadius * i + roundPadding * i,
                    roundBaseline, roundRadius, roundPaint)

        val offSetX = (roundPadding + 2 * roundRadius) * positionOffset
        //绘制一个随着滑动而移动的圆点(与上面的圆相比就多了一个offSetX偏移，实现动态移动)
        canvas?.drawCircle(startX + 2 * roundRadius * selectedPosition + roundPadding * selectedPosition + offSetX,
                roundBaseline, roundRadius, selectedRoundPaint)

        var textWidth: Float = -1f
        textPaint.alpha = (255 * (1 - Math.abs(positionOffset))).toInt()
        if (positionOffset >= 0) {
            //只要不是滑动到最右边的position
            if (selectedPosition < titles.size - 1) {
                //当前title和下一个title的宽度之和
                textWidth = textPaint.measureText(titles[selectedPosition]) + textPaint.measureText(titles[selectedPosition + 1])
                canvas?.drawText(titles[selectedPosition + 1], width / 2 + textWidth / 2 - textWidth / 2 * positionOffset,
                        textBaseLine, textPaint)
            } else {
                canvas?.drawText(titles[selectedPosition], width / 2 - textWidth / 2 * positionOffset,
                        textBaseLine, textPaint)
            }
        }//停止滑动
        else {
            if (selectedPosition > 0) {
                textWidth = textPaint.measureText(titles[selectedPosition]) + textPaint.measureText(titles[selectedPosition - 1])
                canvas?.drawText(titles[selectedPosition], width / 2 - textWidth / 2 * positionOffset,
                        textBaseLine, textPaint)
                textPaint.alpha = (255 * Math.abs(positionOffset)).toInt()
                canvas?.drawText(titles[selectedPosition - 1], width / 2 - textWidth / 2 * positionOffset,
                        textBaseLine, textPaint)
            }
        }
    }

    /*  pagerChangeListener*/
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        when{
            position>=selectedPosition->{
                //1->2->3 向右滑动
                this.positionOffset=positionOffset
            }
            position<selectedPosition ->{
                //1<-2<-3 向左滑动
                this.positionOffset=1-positionOffset
            }
        }
        invalidate()
    }

    override fun onPageSelected(position: Int) {
        selectedPosition=position
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
    /*  pagerChangeListener*/


    var viewPager: ViewPager? = null
        set(value) {
            if (field == value || value == null)
                return
            //将之前引用的viewPager清空
            field?.clearOnPageChangeListeners()
            field = value
            with(field!!){
                adapter ?: throw IllegalStateException("ViewPager doesn't have a adapter instance")
                addOnPageChangeListener(this@SimplePagerIndicator1)
            }
            notifyDataSetChanged()
        };

    fun notifyDataSetChanged(){
        selectedPosition=0
        titles.clear()
        val adapter=viewPager?.adapter
        for (i in 0 until adapter?.count!!){
            titles.add(adapter.getPageTitle(i).toString())
        }
        invalidate()
    }
}