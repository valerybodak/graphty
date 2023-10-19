package com.vbodak.graphtylib.graph.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

abstract class BaseGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val valueTextPaint: TextPaint by lazy {
        val paint = TextPaint()
        paint.isAntiAlias = true
        paint.textSize = getGraphParams().valueTextSize
        paint.color = getColor(getGraphParams().valueTextColor)
        paint
    }

    private val titlePaint: Paint by lazy {
        val paint = TextPaint()
        paint.isAntiAlias = true
        paint.textSize = getGraphParams().titleTextSize
        paint.color = getColor(getGraphParams().titleTextColor)
        paint
    }

    abstract fun getMinValue(): Int
    abstract fun getMaxValue(): Int
    abstract fun getGraphParams(): BaseGraphParams
    abstract fun getVerticalDivisionWidth(): Float

    protected fun getColor(@ColorRes colorResource: Int): Int {
        return ContextCompat.getColor(context, colorResource)
    }

    protected fun drawScaleValues(canvas: Canvas) {
        val minValue = getMinValue()
        val maxValue = getMaxValue()
        val midValue = minValue + (maxValue - minValue) / 2

        //draw max value
        val maxValueTop = getValueTextBound(maxValue).height().toFloat()
        drawValue(canvas, maxValueTop, maxValue)

        //draw min value
        val minValueTop = height - getGraphParams().titleScaleHeightPx
        drawValue(canvas, minValueTop, minValue)

        //draw mid value
        val midValueTop = (height - getGraphParams().titleScaleHeightPx) / 2F + (getValueTextBound(midValue).height() / 2F)
        drawValue(canvas, midValueTop, midValue)
    }

    private fun drawValue(canvas: Canvas, valueTop: Float, value: Int) {
        val valuePaint = valueTextPaint
        val textBound = Rect()
        val valueTitle = value.toString()
        valuePaint.getTextBounds(valueTitle, 0, valueTitle.length, textBound)

        val valueLeft = getGraphParams().valueScaleWidthPx - textBound.width()
        canvas.drawText(
            valueTitle,
            valueLeft,
            valueTop,
            valuePaint
        )
    }

    protected fun drawScaleTitles(canvas: Canvas, titles: List<String>) {
        val divisionWidth = getVerticalDivisionWidth()
        for (index in titles.indices) {
            val divisionLeft = index * divisionWidth + getGraphParams().valueScaleWidthPx

            val textBoundTitle = Rect()
            val valueTitle = titles[index]
            titlePaint.getTextBounds(valueTitle, 0, valueTitle.length, textBoundTitle)

            val titleTop = height - (getGraphParams().titleScaleHeightPx / 2) + (textBoundTitle.height() / 2)

            val titleLeft =
                divisionLeft + (divisionWidth / 2) - (textBoundTitle.width() / 2)

            canvas.drawText(
                valueTitle,
                titleLeft,
                titleTop,
                titlePaint
            )
        }
    }

    protected fun getValueTextBound(value: Int): Rect {
        val valuePaint = valueTextPaint
        val textBound = Rect()
        val valueTitle = value.toString()
        valuePaint.getTextBounds(valueTitle, 0, valueTitle.length, textBound)
        return textBound
    }

    private fun getValueScaleTextHeight(): Int {
        //It can be any value because we use it only to measure text's container height
        val title = "0"
        val textPaint = valueTextPaint
        val textBound = Rect()
        textPaint.getTextBounds(title, 0, title.length, textBound)
        return textBound.height()
    }

    protected fun getGraphHeight(): Float {
        return getGraphBottom() - getGraphTop()
    }

    protected fun getGraphTop(): Float {
        return (getValueScaleTextHeight() / 2F)
    }

    protected fun getGraphBottom(): Float {
        return height - getGraphParams().titleScaleHeightPx - (getValueScaleTextHeight() / 2F)
    }
}