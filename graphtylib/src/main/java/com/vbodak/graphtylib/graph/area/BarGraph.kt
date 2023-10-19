package com.vbodak.graphtylib.graph.area

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

class BarGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint: Paint by lazy {
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint
    }

    private val valueTextPaint: TextPaint by lazy {
        val paint = TextPaint()
        paint.isAntiAlias = true
        paint.textSize = params.valueTextSize
        paint.color = getColor(params.valueTextColor)
        paint
    }

    private val titlePaint: Paint by lazy {
        val paint = TextPaint()
        paint.isAntiAlias = true
        paint.textSize = params.titleTextSize
        paint.color = getColor(params.titleTextColor)
        paint
    }

    private var bars: List<Bar> = emptyList()
    private var params: BarGraphParams = BarGraphParams()

    fun setup(params: BarGraphParams) {
        this.params = params
    }

    fun draw(bars: List<Bar>) {
        this.bars = bars
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (bars.isNotEmpty()) {
            drawBars(canvas)
            drawScaleValues(canvas)
            drawScaleTitles(canvas)
        }
    }

    private fun drawBars(canvas: Canvas) {
        val divisionWidth = getVerticalDivisionWidth()
        for (barIndex in bars.indices) {
            val bar = bars[barIndex]
            for (valueIndex in bar.values.indices) {
                if (params.barColors.size >= bar.values.size) {
                    //draw bar only in case the bar's color is set
                    val value = bar.values[valueIndex]
                    val barColor = getColor(params.barColors[valueIndex])
                    val divisionLeft = barIndex * divisionWidth + params.valueScaleWidthPx
                    val barLeft = divisionLeft + (divisionWidth / 2F) - params.barWidthPx / 2F
                    val barTop = getBarTop(value)
                    val barRight = barLeft + params.barWidthPx
                    barPaint.color = barColor
                    canvas.drawRoundRect(
                        barLeft,
                        barTop,
                        barRight,
                        getGraphBottom(),
                        params.barCornerRadiusPx,
                        params.barCornerRadiusPx,
                        barPaint
                    )
                }
            }
        }
    }

    private fun drawScaleValues(canvas: Canvas) {
        val minValue = getMinValue()
        val maxValue = getMaxValue()
        val midValue = minValue + (maxValue - minValue) / 2

        //draw max value
        val maxValueTop = getValueTextBound(maxValue).height().toFloat()
        drawValue(canvas, maxValueTop, maxValue)

        //draw min value
        val minValueTop = height - params.titleScaleHeightPx
        drawValue(canvas, minValueTop, minValue)

        //draw mid value
        val midValueTop = (height - params.titleScaleHeightPx) / 2F + (getValueTextBound(midValue).height() / 2F)
        drawValue(canvas, midValueTop, midValue)
    }

    private fun drawValue(canvas: Canvas, valueTop: Float, value: Int) {
        val valuePaint = valueTextPaint
        val textBound = Rect()
        val valueTitle = value.toString()
        valuePaint.getTextBounds(valueTitle, 0, valueTitle.length, textBound)

        val valueLeft = params.valueScaleWidthPx - textBound.width()
        canvas.drawText(
            valueTitle,
            valueLeft,
            valueTop,
            valuePaint
        )
    }

    private fun getValueTextBound(value: Int): Rect {
        val valuePaint = valueTextPaint
        val textBound = Rect()
        val valueTitle = value.toString()
        valuePaint.getTextBounds(valueTitle, 0, valueTitle.length, textBound)
        return textBound
    }

    private fun drawScaleTitles(canvas: Canvas) {
        val divisionWidth = getVerticalDivisionWidth()
        for (index in bars.indices) {
            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx

            val textBoundTitle = Rect()
            val valueTitle = bars[index].title
            titlePaint.getTextBounds(valueTitle, 0, valueTitle.length, textBoundTitle)

            val titleTop = height - (params.titleScaleHeightPx / 2) + (textBoundTitle.height() / 2)

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

    private fun getBarTop(value: Int): Float {
        val minValue = getMinValue()
        val maxValue = getMaxValue()
        var y = getGraphTop()
        if (value in minValue..maxValue) {
            val offset = getGraphHeight() / (maxValue - minValue).toFloat()
            y = getGraphBottom() - ((value.toFloat() - minValue.toFloat()) * offset)
        }
        return y
    }

    private fun getVerticalDivisionWidth(): Float {
        return (width - params.valueScaleWidthPx) / bars.size
    }

    private fun getValueScaleTextHeight(): Int {
        //It can be any value because we use it only to measure text's container height
        val title = "0"
        val textPaint = valueTextPaint
        val textBound = Rect()
        textPaint.getTextBounds(title, 0, title.length, textBound)
        return textBound.height()
    }

    private fun getMinValue(): Int {
        val min = bars.map { it.values.min() }.min()
        return if (params.minValue >= min) {
            min
        } else {
            params.minValue
        }
    }

    private fun getMaxValue(): Int {
        val max = bars.map { it.values.max() }.max()
        return if (params.maxValue <= max) {
            max
        } else {
            params.maxValue
        }
    }

    private fun getGraphHeight(): Float {
        return getGraphBottom() - getGraphTop()
    }

    private fun getGraphTop(): Float {
        return (getValueScaleTextHeight() / 2F)
    }

    private fun getGraphBottom(): Float {
        return height - params.titleScaleHeightPx - (getValueScaleTextHeight() / 2F)
    }

    private fun getColor(@ColorRes colorResource: Int): Int {
        return ContextCompat.getColor(context, colorResource)
    }
}