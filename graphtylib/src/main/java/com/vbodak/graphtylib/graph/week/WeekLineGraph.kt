package com.vbodak.graphtylib.graph.week

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.vbodak.graphtylib.common.CommonConst
import java.util.*

enum class NodesMode {
    NONE, ALL, MAX
}

data class Params(
    val minValue: Int = CommonConst.UNDEFINED,
    val maxValue: Int = CommonConst.UNDEFINED,
    val enableGuides: Boolean = true,
    val lineWidth: Float = 5F,
    val guideWidth: Float = 3F,
    val valueScaleWidthPx: Float = 100F,
    val valueTextSize: Float = 36F,
    val weekdayStart: Int = Calendar.MONDAY,
    val weekdayNameMap: Map<Int, String> = emptyMap(),
    val weekdayScaleHeightPx: Float = 70F,
    val weekdayTextSize: Float = 36F,
    val nodesMode: NodesMode = NodesMode.ALL,
    val nodeRadiusPx: Float = 14F,
    @ColorRes
    val lineColor: Int = android.R.color.black,
    @ColorRes
    val guideColor: Int = android.R.color.darker_gray,
    @ColorRes
    val nodeFillColor: Int = android.R.color.white,
    @ColorRes
    val weekdayTextColor: Int = android.R.color.black,
    @ColorRes
    val valueTextColor: Int = android.R.color.black,
)

class WeekLineGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val WEEKDAYS_NUMBER = 7
    }

    private var params: Params = Params()
    private var values: List<Int> = emptyList()

    fun displayValues(params: Params, values: List<Int>) {
        this.params = params
        this.values = values
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (values.isNotEmpty()) {
            if (params.enableGuides) {
                drawGuides(canvas)
            }
            drawLine(canvas)
            drawScaleValues(canvas)
            drawScaleWeekdays(canvas)
            if (params.nodesMode != NodesMode.NONE) {
                drawNodes(canvas)
            }
        }
    }

    private fun drawGuides(canvas: Canvas) {
        val nodePaint = Paint(ANTI_ALIAS_FLAG)
        nodePaint.style = Paint.Style.FILL
        val divisionWidth = getVerticalDivisionWidth()
        for (index in 0 until WEEKDAYS_NUMBER) {
            val item = if (values.size <= index) 0 else values[index]

            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx

            val currentX = divisionLeft + (divisionWidth / 2F)

            var currentY = getGraphTop()
            val maxValue = getMaxValue()
            if (item >= getMinValue() && item <= maxValue) {
                currentY =
                    getGraphBottom() - (getGraphHeight() / (maxValue / item.toFloat()))
            }

            val path = Path()
            path.moveTo(currentX, currentY)
            path.lineTo(currentX, height - params.weekdayScaleHeightPx)

            val p1 = Paint(ANTI_ALIAS_FLAG)
            p1.style = Paint.Style.STROKE
            p1.strokeWidth = params.guideWidth

            val p2 = Paint(p1)
            p2.color = getColor(params.guideColor)
            p2.pathEffect =
                DashPathEffect(floatArrayOf(4F, 8F), 0F)

            canvas.drawPath(path, p2)
        }
    }

    private fun drawLine(canvas: Canvas) {
        val divisionWidth = getVerticalDivisionWidth()
        val linePaint = getLinePaint()
        var prevX = CommonConst.UNDEFINED.toFloat()
        var prevY = CommonConst.UNDEFINED.toFloat()
        for (index in 0 until WEEKDAYS_NUMBER) {
            val item = if (values.size <= index) 0 else values[index]

            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx

            val currentX =
                divisionLeft + (divisionWidth / 2F)

            val maxValue = getMaxValue()

            var currentY = getGraphTop()
            if (item >= getMinValue() && item <= maxValue) {
                currentY =
                    getGraphBottom() - (getGraphHeight() / (maxValue / item.toFloat()))
            }

            if (prevX == CommonConst.UNDEFINED.toFloat() && prevY == CommonConst.UNDEFINED.toFloat()) {
                //the first point
                prevX = currentX
                prevY = currentY
            } else {
                canvas.drawLine(prevX, prevY, currentX, currentY, linePaint)

                prevX = currentX
                prevY = currentY
            }
        }
    }

    private fun drawScaleValues(canvas: Canvas) {
        val minValue = getMinValue()
        val maxValue = getMaxValue()
        val midValue = (maxValue - minValue) / 2

        //draw max value
        val maxValueTop = getValueTextBound(maxValue).height().toFloat()
        drawValue(canvas, maxValueTop, maxValue)

        //draw min value
        val minValueTop = height - params.weekdayScaleHeightPx
        drawValue(canvas, minValueTop, minValue)

        //draw mid value
        val midValueTop = (height - params.weekdayScaleHeightPx) / 2F + (getValueTextBound(midValue).height() / 2F)
        drawValue(
            canvas,
            midValueTop,
            midValue
        )
    }

    private fun drawValue(canvas: Canvas, valueTop: Float, value: Int) {
        val valuePaint = getValueTextPaint()
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
        val valuePaint = getValueTextPaint()
        val textBound = Rect()
        val valueTitle = value.toString()
        valuePaint.getTextBounds(valueTitle, 0, valueTitle.length, textBound)
        return textBound
    }

    private fun drawScaleWeekdays(canvas: Canvas) {
        val divisionWidth = getVerticalDivisionWidth()
        val weekdayTitles = getWeekdayTitleList()
        for (index in 0 until WEEKDAYS_NUMBER) {
            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx

            val weekdayPaint = getWeekdayPaint()
            val textBoundWeekday = Rect()
            val weekdayTitle = weekdayTitles[index]
            weekdayPaint.getTextBounds(weekdayTitle, 0, weekdayTitle.length, textBoundWeekday)

            val weekdayTop = height - (params.weekdayScaleHeightPx / 2) + (textBoundWeekday.height() / 2)

            val weekdayLeft =
                divisionLeft + (divisionWidth / 2) - (textBoundWeekday.width() / 2)

            canvas.drawText(
                weekdayTitle,
                weekdayLeft,
                weekdayTop,
                weekdayPaint
            )
        }
    }

    private fun drawNodes(canvas: Canvas) {
        val nodePaint = Paint(ANTI_ALIAS_FLAG)
        nodePaint.style = Paint.Style.FILL
        val divisionWidth = getVerticalDivisionWidth()
        for (index in 0 until WEEKDAYS_NUMBER) {
            val item = if (values.size <= index) 0 else values[index]

            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx

            val currentX =
                divisionLeft + (divisionWidth / 2F)

            var currentY = getGraphTop()
            val maxValue = getMaxValue()
            if (item >= getMinValue() && item <= maxValue) {
                currentY =
                    getGraphBottom() - (getGraphHeight() / (maxValue / item.toFloat()))
            }

            if (params.nodesMode == NodesMode.ALL || (params.nodesMode == NodesMode.MAX && item == values.max())) {
                //outer circle
                nodePaint.color = getColor(params.lineColor)
                canvas.drawCircle(currentX, currentY, params.nodeRadiusPx, nodePaint)

                //inner circle
                nodePaint.color = getColor(params.nodeFillColor)
                canvas.drawCircle(currentX, currentY, params.nodeRadiusPx - params.lineWidth, nodePaint)
            }
        }
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

    private fun getVerticalDivisionWidth(): Float {
        return (width - params.valueScaleWidthPx) / WEEKDAYS_NUMBER
    }

    private fun getValueScaleTextHeight(): Int {
        //It can be any value because we use it only to measure text's container height
        val title = "0"
        val textPaint = getValueTextPaint()
        val textBound = Rect()
        textPaint.getTextBounds(title, 0, title.length, textBound)
        return textBound.height()
    }

    private fun getLinePaint(): Paint {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = params.lineWidth
        paint.color = getColor(params.lineColor)
        return paint
    }

    private fun getWeekdayPaint(): Paint {
        val paint = TextPaint()
        paint.isAntiAlias = true
        paint.textSize = params.weekdayTextSize
        paint.color = getColor(params.weekdayTextColor)
        return paint
    }

    private fun getValueTextPaint(): TextPaint {
        val textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = params.valueTextSize
        textPaint.color = getColor(params.valueTextColor)
        return textPaint
    }

    private fun getMinValue(): Int {
        return if (params.minValue >= values.min()) {
            values.min()
        } else {
            params.minValue
        }
    }

    private fun getMaxValue(): Int {
        return if (params.maxValue <= values.max()) {
            values.max()
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
        return height - params.weekdayScaleHeightPx - (getValueScaleTextHeight() / 2F)
    }

    private fun getColor(@ColorRes colorResource: Int): Int {
        return ContextCompat.getColor(context, colorResource)
    }
}