package com.example.weather.widget

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.weather.R
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference

/**
 * PagerTitleStrip is a non-interactive indicator of the current, next, and
 * previous pages of a {@link ViewPager}. It is intended to be used as a child
 * view of a ViewPager widget in your XML layout. Add it as a child of a
 * ViewPager in your layout file and set its android:layout_gravity to TOP or
 * BOTTOM to pin it to the top or bottom of the ViewPager. The title from each
 * page is supplied by the method {@link PagerAdapter#getPageTitle(int)} in the
 * adapter supplied to the ViewPager.
 *
 */
/**
 *
 * 根据滑动(基于ViewPager的onPagerChangeListener):
 * 1 改变title
 * 2 移动小圆点，禁止时隐藏
 *
 * 自定义View流程 onMeasure() -> onLayout() -> onDraw()
 * 自定义ViewGroup需要重写上述三个方法，而自定义View只需要重写onDraw()
 */
class PagerTitleStrip @JvmOverloads constructor(context: Context, attributeSet: AttributeSet)
    : ViewGroup(context, attributeSet) {
    //    private var gravity: Int = 0
    var gravity: Int = Gravity.TOP
        set(value) {
            field = value
            requestLayout()
        }
    // Spacing between each title displayed in pixels
    var textSpacing: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var viewPager: ViewPager? = null
        set(value) {
            if (value == null)
                return
            field = value
            with(value) {
                addOnPageChangeListener(mPageListener)
                updateAdapter(if (mWatchingAdapter != null) mWatchingAdapter?.get() else null, adapter)
            }
        }
    private var mWatchingAdapter: WeakReference<PagerAdapter>? = null

    private var mLastKnownCurrentPage = -1
    private var mLastKnownPositionOffset = -1F

    private var mUpdatingText: Boolean = false
    private var mUpdatingPositions: Boolean = false //当前正在更新position
    private val mPageListener = PageListener()

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.textAppearance, android.R.attr.textColor, android.R.attr.gravity)
        private val TEXT_ATTRS = intArrayOf(0x0101038c)// android.R.attr.textAllCaps
        private val SIDE_ALPHA = 0.6f //wangbin changed this
        private val TEXT_SPACING = 0 //16  // dip 这个是每个TextView的间隔    //wangbin changed this
    }

    interface PagerTitleStripImpl {
        fun setSingleLineAllCaps(textView: TextView)
    }

    inner class PagerTitleStripImplBase : PagerTitleStripImpl {
        override fun setSingleLineAllCaps(textView: TextView) {
            textView.setSingleLine()
        }
    }

    private val mPrevTv by lazy { TextView(context) }
    private val mCurrTv by lazy { TextView(context) }
    private val mNextTv by lazy { TextView(context) }

    private var mTextColor: Int
    private var mCircleIndicator: CircleIndicator

    //添加三个TextView和一个CircleIndicator
    init {
        addView(mPrevTv)
        addView(mCurrTv)
        addView(mNextTv)

        //获得自定义属性: textAppearance，textSize，textColor，获取gravity
        val a = context.obtainStyledAttributes(attributeSet, com.example.weather.widget.PagerTitleStrip.Companion.ATTRS)
        with(a) {
            val textAppearance = getResourceId(0, 0)
            if (textAppearance != 0)
                setTextAppearance(textAppearance, mPrevTv, mCurrTv, mNextTv)
            val textSize = getDimensionPixelSize(1.toInt(), 0)
            if (textSize != 0)
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            if (hasValue(2.toInt())) {
                val textColor = getColor(2.toInt(), 0)
                mPrevTv.textColor = textColor
                setTextColor(textColor, mPrevTv, mCurrTv, mNextTv)
            }
            gravity = getResourceId(3.toInt(), android.view.Gravity.BOTTOM)
            recycle()
            var allCaps = false
            if (textAppearance != 0) {
                val ta = context.obtainStyledAttributes(textAppearance, TEXT_ATTRS)
                allCaps = ta.getBoolean(0, false)
                ta.recycle()
            }

            if (!allCaps)
                setSingleLine(mPrevTv, mCurrTv, mNextTv)
        }

        mTextColor = mCurrTv.textColors.defaultColor
        setEllipsize(TextUtils.TruncateAt.END, mPrevTv, mCurrTv, mNextTv)
        val density = context.resources.displayMetrics.density
        textSpacing = (TEXT_SPACING * density).toInt()

        mCircleIndicator = CircleIndicator(context, attributeSet)
        addView(mCircleIndicator)
    }

    private inner class CircleIndicator(context: Context, attributeSet: AttributeSet)
        : View(context, attributeSet), ViewPager.OnPageChangeListener {

        //circleIndicator default value
        private val DEFAULT_INDICATOR_RADIUS = 10
        private val DEFAULT_INDICATOR_MARGIN = 40
        private val DEFAULT_INDICATOR_BACKGROUND = Color.BLUE
        private val DEFAULT_INDICATOR_SELECTED_BACKGROUND = Color.RED

        /*     FOR circleIndicator config*/
        private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var mCurItemPosition: Int = 0
        private var mCurItemPositionOffset: Float = 0F
        private var mIndicatorRadius: Int = 0
        private var mIndicatorMargin: Int = DEFAULT_INDICATOR_MARGIN
        private var mIndicatorBackground: Int = DEFAULT_INDICATOR_BACKGROUND
        private var mIndicatorSelectedBackground: Int = DEFAULT_INDICATOR_SELECTED_BACKGROUND

        internal val fadeDuration = 400L
        internal var isShowing = true

        init {
            val density = context.resources.displayMetrics.density
            mPaint.strokeWidth = 1f * density //笔画宽度
            //获取自定义属性: radius，margin，background，selectedBackground
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PagerTitleStrip)
            with(typedArray) {
                mIndicatorRadius = getDimensionPixelSize(R.styleable.PagerTitleStrip_pts_radius, DEFAULT_INDICATOR_RADIUS)
                mIndicatorMargin = getDimensionPixelSize(R.styleable.PagerTitleStrip_pts_margin, DEFAULT_INDICATOR_MARGIN)
                mIndicatorBackground = getColor(R.styleable.PagerTitleStrip_pts_background, DEFAULT_INDICATOR_BACKGROUND)
                mIndicatorSelectedBackground = getColor(R.styleable.PagerTitleStrip_pts_selected_background, DEFAULT_INDICATOR_SELECTED_BACKGROUND)
                recycle()
            }
            post {
                hideSelf()
            }
        }

        var itemCount = 0
            set(value) {
                field = value
                invalidate()
            }

        fun getIndicatorHeight(): Int =
                (mIndicatorRadius * 2f + 0.5f).toInt()

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            setMeasuredDimension(widthSize, getIndicatorHeight())
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            trigger(position, positionOffset)
        }

        override fun onPageSelected(position: Int) {
            trigger(position, 0F)
        }

        //根据state显示或隐藏
        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager.SCROLL_STATE_IDLE -> hideSelf()
                else -> showSelf()
            }
        }

        //更新当前curPosition和curPositionOffset并重绘
        private fun trigger(position: Int, positionOffset: Float) {
            mCurItemPosition = position
            mCurItemPositionOffset = positionOffset
            invalidate()
        }

        private fun hideSelf() {
            if (isShowing) {
                animate().alpha(0F).setStartDelay((fadeDuration * 3).toLong()).setDuration(fadeDuration).start()
                isShowing = false
            }
        }

        private fun showSelf() {
            if (!isShowing) {
                animate().alpha(1f).setStartDelay(0).setDuration(fadeDuration / 2).start()
                isShowing = true
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.save()
            if (itemCount == 0)
                return
            //圆的直径+margin
            val oneCircleWidth = mIndicatorRadius * 2f + mIndicatorMargin
            val circleWidth = itemCount * oneCircleWidth - mIndicatorMargin //从第一个圆到最后一个圆的总长度
            with(canvas) {
                //起始坐标: 移动到第一个圆点的左边的坐标
                translate((width - circleWidth) / 2F, height / 2F)
                with(mPaint) {
                    //画所有的圆形(个数: itemCount个)
                    style = Paint.Style.FILL
                    color = mIndicatorBackground
                    for (i in 0 until itemCount)
                        drawCircle(i * oneCircleWidth + mIndicatorRadius, 0f, mIndicatorRadius.toFloat(), mPaint)

                    //画当前被选中的圆
                    style = Paint.Style.FILL
                    color = mIndicatorSelectedBackground
                    //通过positionOffset实现动态移动
                    drawCircle(mIndicatorRadius + (mCurItemPosition + mCurItemPositionOffset) * oneCircleWidth, 0f, mIndicatorRadius.toFloat(), mPaint)
                    canvas.restore()
                }
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

//        if (widthMode != MeasureSpec.EXACTLY)
//            throw IllegalStateException("Must measure with an exact width")

        val minHeight = getMinHeight()
        val padding = paddingTop + paddingBottom
        val childHeight = heightSize - padding //child的实际高度

        val childWidthSpec = MeasureSpec.makeMeasureSpec((widthSize * 0.8f).toInt(), MeasureSpec.AT_MOST)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)

        mPrevTv.measure(childWidthSpec, childHeightSpec)
        mCurrTv.measure(childWidthSpec, childHeightSpec)
        mNextTv.measure(childWidthSpec, childHeightSpec)

        mCircleIndicator.measure(widthMeasureSpec, childHeightSpec)
        when (heightMode) {
        //EXACTLY 对应match_parent和具体数值
            MeasureSpec.EXACTLY -> setMeasuredDimension(widthSize, heightSize)
            else -> {
                val textHeight = mCurrTv.measuredHeight
                setMeasuredDimension(widthSize, Math.max(minHeight, textHeight + padding))
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (viewPager != null) {
            val offset = if (mLastKnownPositionOffset >= 0) mLastKnownPositionOffset else 0F
            updateTextPositions(mLastKnownCurrentPage, offset, true)
        }
        val indicatorHeight = mCircleIndicator.getIndicatorHeight()
        mCircleIndicator.layout(0, b - t - indicatorHeight * 2, r - l, b - t - indicatorHeight) //indicator底部留有indicatorHeight的边距: 使这个公式(b-t=indicatorHeight)成立
    }

    private fun getMinHeight(): Int {
        var minHeight = 0
        if (background != null)
            minHeight = background.intrinsicHeight
        return minHeight
    }

    private val mNonPrimaryAlpha: Int = 0

    /**
     * Set the color value used as the base color for all displayed page titles.
     * Alpha will be ignored for non-primary page titles.See{@link #setNonPrimaryAlpha(float)}
     *
     * @param color
     *     Color hex code in 0xAARRGGBB format
     */
    fun setTextColor(color: Int) {
        mTextColor = color
        mCurrTv.textColor = color
        val transparentColor = mNonPrimaryAlpha shl 24 or (mTextColor and 0xFFFFFF)
        mPrevTv.textColor = transparentColor
        mNextTv.textColor = transparentColor
    }

    /**
     * Set the default text size to a given unit and value.See{@link TypedValue}
     * for the possible dimension units.
     *
     * <p>
     *   Example: to set the text size to 14px,use
     *   setTextSize(TypedValue.COMPLEX_UNIT_PX,14);
     * <p>
     *
     * @param unit
     *          The desired dimension unit
     * @param size
     *          The desired size in the given units
     */
    fun setTextSize(unit: Int, size: Float) {
        mPrevTv.setTextSize(unit, size)
        mCurrTv.setTextSize(unit, size)
        mNextTv.setTextSize(unit, size)
    }

    /*除非写一个相对应的ViewPager，不然以下两个方法不用改*/
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

//        if (!(parent is ViewPager))
//            throw IllegalStateException("PagerTitleStrip must be a direct child of a ViewPager")
//
//        val pager = parent as ViewPager
//        val adapter = pager.adapter
//
//        with(pager) {
//            addOnPageChangeListener(mPageListener)
//            viewPager = pager
//            updateAdapter(if (mWatchingAdapter != null) mWatchingAdapter?.get() else null,adapter)
//        }
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
//        if (viewPager != null) {
//            updateAdapter(viewPager?.adapter, null)
//            viewPager!!.addOnPageChangeListener(null)
////            viewPager.setOnAdapterChangeListener(null)
//            viewPager = null
//        }
    }
    /*除非写一个相对应的ViewPager，不然以下两个方法不用改*/

   private fun updateText(currentItem: Int, adapter: PagerAdapter) {
        val itemCount = adapter.count
        mUpdatingText = true

        var text: CharSequence? = null
        if (currentItem > 1)
            text = adapter.getPageTitle(currentItem - 1)
        mPrevTv.text = text

        mCurrTv.text = if (currentItem < itemCount) adapter.getPageTitle(currentItem) else null

        text = null
        if (currentItem + 1 < itemCount)
            text = adapter.getPageTitle(currentItem + 1)
        mNextTv.text = text

        //Measure everything
        val width = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        val childWidthSpec = MeasureSpec.makeMeasureSpec((width * 0.8).toInt(), MeasureSpec.AT_MOST)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
        mPrevTv.measure(childWidthSpec, childHeightSpec)
        mCurrTv.measure(childWidthSpec, childHeightSpec)
        mNextTv.measure(childWidthSpec, childHeightSpec)

        mLastKnownCurrentPage = currentItem

        if (!mUpdatingPositions)
            updateTextPositions(currentItem, mLastKnownPositionOffset, false)

        mUpdatingText = false
    }

    private fun convertTextColor(percent: Float): Int {
        val alpha = (percent * 255).toInt() and 0xFF
        return alpha shl 24 or (mTextColor and 0xFFFFFF)
    }

    override fun requestLayout() {
        if (!mUpdatingText)
            super.requestLayout()
    }

   private fun updateTextPositions(position: Int, positionOffset: Float, force: Boolean) {
        //position是已经确定了的当前item，也就是currTv
        //onPageScrolled中positionOffset>0.5f时position会+1
        when {
            //当位置发生改变，更新text
            position != mLastKnownCurrentPage -> updateText(position, viewPager!!.adapter!!)
            //如果offset没有变化，并且!force，直接return
            !force && positionOffset == mLastKnownPositionOffset -> return
        }
        if (positionOffset == 0F) {
            mPrevTv.textColor = convertTextColor(0f)
            mCurrTv.textColor = convertTextColor(1f)
            mNextTv.textColor = convertTextColor(0f)
        } else {
//            val currentPercent = positionOffset //positionOffset=0.6 则currOffset=0.1
            if (positionOffset <= 0.5f) {//变得在出现
                mPrevTv.textColor = convertTextColor(0f)
                mCurrTv.textColor = convertTextColor(1f - positionOffset)
                mNextTv.textColor = convertTextColor(positionOffset)
            } else {
                mPrevTv.textColor = convertTextColor(1f - positionOffset)
                mCurrTv.textColor = convertTextColor(positionOffset)
                mNextTv.textColor = convertTextColor(0f)
            }
        }

        mUpdatingPositions = true

        val prevWidth = mPrevTv.measuredWidth
        val currWidth = mCurrTv.measuredWidth
        val nextWidth = mNextTv.measuredWidth
        val halfCurrWidth = 0 //curWidth/2

        var currOffset = positionOffset + 0.5f //positionOffset=0.6 则currOffset = 0.1
        if (currOffset > 1f)
            currOffset -= 1f  //currOffset>1,就-1

        val currCenter = width / 2 - currWidth * (currOffset - 0.5f)
        val currLeft = currCenter - currWidth / 2
        val currRight = currLeft + currWidth

        val prevBaseLine = mPrevTv.baseline
        val currBaseLine = mCurrTv.baseline
        val nextBaseLine = mNextTv.baseline
        val maxBaseline = Math.max(Math.max(prevBaseLine, currBaseLine), nextBaseLine)
        val prevTopOffset = maxBaseline - prevBaseLine
        val currTopOffset = maxBaseline - currBaseLine
        val nextTopOffset = maxBaseline - nextBaseLine
        val alignedPrevHeight = prevTopOffset + mPrevTv.measuredHeight
        val alignedCurrHeight = (currOffset + mCurrTv.measuredHeight).toInt()
        val alignedNextHeight = nextTopOffset + mNextTv.measuredHeight
        val maxTextHeight = Math.max(Math.max(alignedPrevHeight, alignedCurrHeight), alignedNextHeight)

        val vgrav = gravity and Gravity.VERTICAL_GRAVITY_MASK

        var prevTop = 0
        var currTop = 0
        var nextTop = 0
        when (vgrav) {
            Gravity.TOP -> {
                prevTop = paddingTop + prevTopOffset
                currTop = paddingTop + currTopOffset
                nextTop = paddingTop + nextTopOffset
            }
            Gravity.CENTER_VERTICAL -> {
                val paddedHeight = height - paddingTop - paddingBottom
                val centeredTop = (paddedHeight - maxTextHeight) / 2
                prevTop = centeredTop + prevTopOffset
                currTop = centeredTop + currTopOffset
                nextTop = centeredTop + nextTopOffset
            }
            Gravity.BOTTOM -> {
                val bottomGravTop = height - paddingBottom - maxTextHeight
                prevTop = bottomGravTop + prevTopOffset
                currTop = bottomGravTop + currTopOffset
                nextTop = bottomGravTop + nextTopOffset
            }
        }

        mCurrTv.layout(currLeft.toInt(), currTop, currRight.toInt(), currTop + mCurrTv.measuredHeight)

        val prevLeft = currLeft - prevWidth
        mPrevTv.layout(prevLeft.toInt(), prevTop, (prevLeft + prevWidth).toInt(), prevTop + mPrevTv.measuredHeight)

        val nextLeft = currRight
        mNextTv.layout(nextLeft.toInt(), nextTop, (nextLeft + nextWidth).toInt(), nextTop + mNextTv.measuredHeight)

        mLastKnownPositionOffset = positionOffset
        mUpdatingPositions = false
    }

    fun updateAdapter(oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mPageListener)
            mWatchingAdapter = null
        }
        if (newAdapter != null) {
            newAdapter.registerDataSetObserver(mPageListener)
            mWatchingAdapter = WeakReference(newAdapter)
            mCircleIndicator.itemCount = newAdapter.count
        }
        if (viewPager != null) {
            mLastKnownCurrentPage = -1
            mLastKnownPositionOffset = -1f
            if (newAdapter != null) {
                updateText(viewPager!!.currentItem, newAdapter)
            }
            requestLayout()
        }
    }

    private fun setSingleLine(vararg textView: TextView) {
        for (text in textView)
            text.setSingleLine()
    }

    private fun setEllipsize(end: TextUtils.TruncateAt, vararg textView: TextView) {
        for (text in textView)
            text.ellipsize = end
    }

    private fun setTextColor(textColor: Int, vararg textView: TextView) {
        for (text in textView)
            text.textColor = textColor
    }

    private fun setTextAppearance(textAppearance: Int, vararg textView: TextView) {
        for (text in textView)
            text.setTextAppearance(context, textAppearance) //因为新方法只支持6.0及以上
    }

    private inner class PageListener : DataSetObserver(), ViewPager.OnPageChangeListener {
        private var mScrollState = ViewPager.SCROLL_STATE_IDLE

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            mCircleIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            var position = position
            //Consider ourselves to be on the next page when we're 50% of
            //the way there.
            if (positionOffset > 0.5f)
                position++
            updateTextPositions(position, positionOffset, false)
        }

        override fun onPageSelected(position: Int) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                //Only addData the text there if we're not dragging or setting
                updateText(viewPager?.currentItem!!, viewPager?.adapter!!)

                val offset = if (mLastKnownPositionOffset >= 0) mLastKnownPositionOffset else 0F
                updateTextPositions(viewPager!!.currentItem, offset, true)
            }
            mCircleIndicator.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            mScrollState = state
            mCircleIndicator.onPageScrollStateChanged(state)
        }


        override fun onChanged() {
            updateText(viewPager!!.currentItem, viewPager!!.adapter!!)

            val offset = if (mLastKnownPositionOffset >= 0) mLastKnownPositionOffset else 0F
            updateTextPositions(viewPager!!.currentItem, offset, true)
        }

    }

}