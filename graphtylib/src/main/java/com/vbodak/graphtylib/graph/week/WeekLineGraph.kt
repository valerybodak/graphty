package com.vbodak.graphtylib.graph.week

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.util.AttributeSet
import com.vbodak.graphtylib.graph.base.BaseGraph
import com.vbodak.graphtylib.graph.base.BaseGraphParams
import java.util.*

class WeekLineGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseGraph(context, attrs, defStyleAttr) {

    companion object {
        private const val WEEKDAYS_NUMBER = 7
    }

    private var params: WeekLineGraphParams = WeekLineGraphParams()
    private var values: List<Int> = emptyList()

    private val linePaint: Paint by lazy {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = params.lineWidth
        paint.color = getColor(params.lineColor)
        paint
    }

    private val guidelinePaint: Paint by lazy {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = params.guidelineWidth
        paint.color = getColor(params.guidelineColor)
        paint.pathEffect =
            DashPathEffect(floatArrayOf(4F, 8F), 0F)
        paint
    }

    private val nodePaint: Paint by lazy {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint
    }

    fun setup(params: WeekLineGraphParams) {
        this.params = params
    }

    fun draw(values: List<Int>) {
        this.values = values
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (values.isNotEmpty()) {
            if (params.enableGuidelines) {
                drawGuidelines(canvas)
            }
            drawLine(canvas)
            drawScaleValues(canvas)
            drawScaleTitles(canvas, getWeekdayTitleList())
            if (params.nodesMode != NodesMode.NONE) {
                drawNodes(canvas)
            }
        }
    }

    private fun getGraphPoints(pointCallback: (index: Int, value: Int, x: Float, y: Float) -> Unit) {
        val divisionWidth = getVerticalDivisionWidth()
        for (index in 0 until WEEKDAYS_NUMBER) {
            val value = if (values.size <= index) 0 else values[index]
            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx
            val currentX = divisionLeft + (divisionWidth / 2F)
            val currentY = getPointY(value)
            pointCallback.invoke(index, value, currentX, currentY)
        }
    }

    private fun drawGuidelines(canvas: Canvas) {
        getGraphPoints { _, _, x, y ->
            val path = Path()
            path.moveTo(x, y)
            path.lineTo(x, height - params.titleScaleHeightPx)
            canvas.drawPath(path, guidelinePaint)
        }
    }

    private fun drawLine(canvas: Canvas) {
        val path = Path()
        getGraphPoints { index, _, x, y ->
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
                canvas.drawPath(path, linePaint)
            }
        }
    }

    private fun drawNodes(canvas: Canvas) {
        getGraphPoints { _, value, x, y ->
            if (params.nodesMode == NodesMode.ALL || (params.nodesMode == NodesMode.MAX && value == values.max())) {
                //outer circle
                nodePaint.color = getColor(params.lineColor)
                canvas.drawCircle(x, y, params.nodeRadiusPx, nodePaint)

                //inner circle
                nodePaint.color = getColor(params.nodeFillColor)
                canvas.drawCircle(x, y, params.nodeRadiusPx - params.lineWidth, nodePaint)
            }
        }
    }

    private fun getPointY(value: Int): Float {
        val minValue = getMinValue()
        val maxValue = getMaxValue()
        var y = getGraphTop()
        if (value in minValue..maxValue) {
            val offset = getGraphHeight() / (maxValue - minValue).toFloat()
            y = getGraphBottom() - ((value.toFloat() - minValue.toFloat()) * offset)
        }
        return y
    }

    private fun getWeekdayTitleList(): List<String> {
        val titles = mutableListOf<String>()
        val currentDay = Calendar.getInstance()
        while (titles.size != WEEKDAYS_NUMBER) {
            currentDay.add(Calendar.DAY_OF_WEEK, 1)
            val weekday = currentDay.get(Calendar.DAY_OF_WEEK)
            if (weekday == params.weekdayStart) {
                titles.add(resolveWeekdayName(weekday))
            } else {
                if (titles.size > 0) {
                    titles.add(resolveWeekdayName(weekday))
                }
            }
        }
        return titles
    }

    private fun resolveWeekdayName(weekday: Int): String {
        return params.weekdayNameMap[weekday].toString()
    }

    override fun getVerticalDivisionWidth(): Float {
        return (width - params.valueScaleWidthPx) / WEEKDAYS_NUMBER
    }

    override fun getMinValue(): Int {
        return if (params.minValue >= values.min()) {
            values.min()
        } else {
            params.minValue
        }
    }

    override fun getMaxValue(): Int {
        return if (params.maxValue <= values.max()) {
            values.max()
        } else {
            params.maxValue
        }
    }

    override fun getGraphParams(): BaseGraphParams {
        return params
    }
}