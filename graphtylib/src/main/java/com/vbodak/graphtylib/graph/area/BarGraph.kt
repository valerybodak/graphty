package com.vbodak.graphtylib.graph.area

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import com.vbodak.graphtylib.graph.base.BaseGraph
import com.vbodak.graphtylib.graph.base.BaseParams

class BarGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseGraph(context, attrs, defStyleAttr) {

    private val barPaint: Paint by lazy {
        val paint = Paint()
        paint.style = Paint.Style.FILL
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
            drawScaleTitles(canvas, bars.map { it.title })
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

    override fun getMinValue(): Int {
        val min = bars.minOfOrNull { it.values.min() } ?: 0
        return if (params.minValue >= min) {
            min
        } else {
            params.minValue
        }
    }

    override fun getMaxValue(): Int {
        val max = bars.maxOfOrNull { it.values.max() } ?: 0
        return if (params.maxValue <= max) {
            max
        } else {
            params.maxValue
        }
    }

    override fun getGraphParams(): BaseParams {
        return params
    }

    override fun getVerticalDivisionWidth(): Float {
        return (width - params.valueScaleWidthPx) / bars.size
    }
}