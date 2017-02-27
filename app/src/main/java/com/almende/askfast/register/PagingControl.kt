/**
 * Created by Freeware Sys on 8/19/2016.
 */

package com.almende.askfast.register;

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.shapes.Shape
import android.graphics.drawable.shapes.RectShape

/**
 * View which has circle-formed page indicator.
 *
 * @author Soichiro Kashima
 */
open class PagingControl(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs, 0) {
    private var mNumOfPages: Int
    private var mIndicatorSize: Float
    private var mCurrentIndicatorSize: Float
    private var mIndicatorDistance: Float
    private var mIndicatorShape: Int
    private var mColorCurrentDefault: Int
    private var mColorCurrentPressed: Int
    private var mColorNormalDefault: Int
    private var mColorNormalPressed: Int
    private var mCurrentPage: Int

    companion object {
        private val DEFAULT_INDICATOR_SIZE = 4.0.toFloat()
        private val DEFAULT_INDICATOR_DISTANCE = 4.0.toFloat()
        private val INDICATOR_SHAPE_CIRCLE = 0
        private val INDICATOR_SHAPE_RECTANGLE = 1
    }

    init {
        mNumOfPages = 3
        mIndicatorSize = 0.toFloat()
        mIndicatorDistance = 0.toFloat()
        mCurrentIndicatorSize = 0.toFloat()
        mIndicatorShape = INDICATOR_SHAPE_CIRCLE
        mColorCurrentDefault = 0
        mColorCurrentPressed = 0
        mColorNormalDefault = 0
        mColorNormalPressed = 0
        mCurrentPage = 0

        // Horizontal layout by default
        if (getOrientation() != VERTICAL) {
            setOrientation(HORIZONTAL)
        }
        setGravity(Gravity.CENTER)

        val r: Resources? = getResources()
        if (r != null) {
            val a: TypedArray? = context.getTheme()?.obtainStyledAttributes(attrs, R.styleable.AndroidPageControl, R.attr.apcStyles, 0)
            if (a != null) {
                val indicatorSizeDefault = DEFAULT_INDICATOR_SIZE * r.getDisplayMetrics().density
                var indicatorSize = a.getDimension(R.styleable.AndroidPageControl_apc_indicatorSize, indicatorSizeDefault)
                if (indicatorSize <= 0) {
                    indicatorSize = indicatorSizeDefault
                }
                mIndicatorSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorSize, r.getDisplayMetrics())

                val indicatorDistanceDefault = DEFAULT_INDICATOR_DISTANCE * r.getDisplayMetrics().density
                var indicatorDistance = a.getDimension(R.styleable.AndroidPageControl_apc_indicatorDistance, indicatorDistanceDefault)
                if (indicatorDistance <= 0) {
                    indicatorDistance = indicatorDistanceDefault
                }
                mIndicatorDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorDistance, r.getDisplayMetrics())

                val currentIndicatorSizeDefault = indicatorSize
                var currentIndicatorSize = a.getDimension(R.styleable.AndroidPageControl_apc_currentIndicatorSize, currentIndicatorSizeDefault)
                if (currentIndicatorSize <= 0) {
                    currentIndicatorSize = currentIndicatorSizeDefault
                }
                mCurrentIndicatorSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, currentIndicatorSize, r.getDisplayMetrics())

                mIndicatorShape = a.getInt(R.styleable.AndroidPageControl_apc_indicatorShape, INDICATOR_SHAPE_CIRCLE)
                if (mIndicatorShape < INDICATOR_SHAPE_CIRCLE || INDICATOR_SHAPE_RECTANGLE < mIndicatorShape) {
                    mIndicatorShape = INDICATOR_SHAPE_CIRCLE
                }

                mColorCurrentDefault = a.getColor(R.styleable.AndroidPageControl_apc_colorCurrentDefault, r.getColor(R.color.apc_indicator_current_default))
                mColorCurrentPressed = a.getColor(R.styleable.AndroidPageControl_apc_colorCurrentPressed, r.getColor(R.color.apc_indicator_current_pressed))
                mColorNormalDefault = a.getColor(R.styleable.AndroidPageControl_apc_colorNormalDefault, r.getColor(R.color.apc_indicator_normal_default))
                mColorNormalPressed = a.getColor(R.styleable.AndroidPageControl_apc_colorNormalPressed, r.getColor(R.color.apc_indicator_normal_pressed))

                a.recycle()
            }
        }
    }

    public fun setPosition(position: Int) {
        mCurrentPage = position
        refresh()
    }

    public fun refresh() {
        removeAllViews()
        for (i in 0..mNumOfPages - 1) {
            val b = Button(getContext())
            setIndicatorBackground(b, i == mCurrentPage)
            var isCurrent: Boolean = (i == mCurrentPage)
            val lp = if (isCurrent) {
                LayoutParams(mCurrentIndicatorSize.toInt(), mCurrentIndicatorSize.toInt())
            } else {
                LayoutParams(mIndicatorSize.toInt(), mIndicatorSize.toInt())
            }
            var margin: Int
            if (isCurrent) {
                margin = ((mIndicatorDistance - (mCurrentIndicatorSize - mIndicatorSize)) / 2).toInt()
            } else {
                margin = (mIndicatorDistance / 2).toInt()
            }
            if (getOrientation() == HORIZONTAL) {
                lp.leftMargin = margin
                lp.rightMargin = margin
            } else {
                lp.topMargin = margin
                lp.bottomMargin = margin
            }
            b.setTag(i)
            addView(b, lp)
        }
        // Set current clickable state to new children
        requestLayout()
    }

    public fun updateNumOfPages(num: Int) {
        mNumOfPages = num
        removeAllViews()
        for (i in 0..mNumOfPages - 1) {
            val b = Button(getContext())
            setIndicatorBackground(b, i == mCurrentPage)
            var isCurrent: Boolean = (i == mCurrentPage)
            val lp = if (isCurrent) {
                LayoutParams(mCurrentIndicatorSize.toInt(), mCurrentIndicatorSize.toInt())
            } else {
                LayoutParams(mIndicatorSize.toInt(), mIndicatorSize.toInt())
            }
            var margin: Int
            if (isCurrent) {
                margin = ((mIndicatorDistance - (mCurrentIndicatorSize - mIndicatorSize)) / 2).toInt()
            } else {
                margin = (mIndicatorDistance / 2).toInt()
            }
            if (getOrientation() == HORIZONTAL) {
                lp.leftMargin = margin
                lp.rightMargin = margin
            } else {
                lp.topMargin = margin
                lp.bottomMargin = margin
            }
            b.setTag(i)
            addView(b, lp)
        }
        // Set current clickable state to new children
        requestLayout()
    }

    private fun setIndicatorBackground(b: Button, isCurrent: Boolean) {
        val drawableDefault = ShapeDrawable()
        drawableDefault.setShape(getIndicatorShape())
        drawableDefault.getPaint()?.setColor(if (isCurrent) mColorCurrentDefault else mColorNormalDefault)
        val drawablePressed = ShapeDrawable()
        drawablePressed.setShape(getIndicatorShape())
        drawablePressed.getPaint()?.setColor(if (isCurrent) mColorCurrentPressed else mColorNormalPressed)

        val sld = StateListDrawable()
        var statesPressed = IntArray(1)
        statesPressed.set(0, android.R.attr.state_pressed)
        sld.addState(statesPressed, drawablePressed)
        var statesDefault = IntArray(1)
        statesDefault.set(0, -android.R.attr.state_pressed)
        sld.addState(statesDefault, drawableDefault)
        if (Build.VERSION.SDK_INT < 16) {
            b.setBackgroundDrawable(sld)
        } else {
            b.setBackground(sld)
        }
    }

    private fun getIndicatorShape(): Shape {
        when (mIndicatorShape) {
            INDICATOR_SHAPE_RECTANGLE -> return RectShape()
            else -> return OvalShape()
        }
    }
}
