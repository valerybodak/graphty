package com.vbodak.graphtylib.graph.area

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.util.*

class AreaGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val WEEKDAYS_NUMBER = 7
    }

    private var areas: List<Area> = emptyList()
    private var params: AreaGraphParams = AreaGraphParams()
    //private var values: List<Int> = emptyList()

    fun setup(params: AreaGraphParams) {
        this.params = params
    }

    fun draw(areas: List<Area>) {
        this.areas = areas
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (areas.isNotEmpty()) {
            for(area in areas){
                getGraphPoints(area) { index, _, x, y ->
                    val path = Path()
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        canvas.drawPath(path, getLinePaint())
                    }
                }
            }
            drawLine(canvas)
            drawScaleValues(canvas)
            drawScaleWeekdays(canvas)
        }
    }

    private fun getGraphPoints(area: Area, pointCallback: (index: Int, value: Int, x: Float, y: Float) -> Unit) {
        val divisionWidth = getVerticalDivisionWidth()
        for (index in area.values.indices) {
            val value = area.values[index]
            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx
            val currentX = divisionLeft + (divisionWidth / 2F)
            val currentY = getPointY(value)
            pointCallback.invoke(index, value, currentX, currentY)
        }
    }

    private fun drawLine(canvas: Canvas) {
        /*val path = Path()
        getGraphPoints { index, _, x, y ->
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
                canvas.drawPath(path, getLinePaint())
            }
        }*/
    }

    private fun drawScaleValues(canvas: Canvas) {
        val minValue = getMinValue()
        val maxValue = getMaxValue()
        val midValue = minValue + (maxValue - minValue) / 2

        //draw max value
        val maxValueTop = getValueTextBound(maxValue).height().toFloat()
        drawValue(canvas, maxValueTop, maxValue)

        //draw min value
        val minValueTop = height - params.weekdayScaleHeightPx
        drawValue(canvas, minValueTop, minValue)

        //draw mid value
        val midValueTop = (height - params.weekdayScaleHeightPx) / 2F + (getValueTextBound(midValue).height() / 2F)
        drawValue(canvas, midValueTop, midValue)
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
        return areas.map { it.values.min() }.min()
    }

    private fun getMaxValue(): Int {
        return areas.map { it.values.max() }.max()
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