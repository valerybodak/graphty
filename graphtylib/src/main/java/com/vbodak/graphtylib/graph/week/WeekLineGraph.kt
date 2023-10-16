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
    /**
     * Min value to display on scale
     */
    val minValue: Int = CommonConst.UNDEFINED,
    /**
     * Max value to display on scale
     */
    val maxValue: Int = CommonConst.UNDEFINED,
    val enableGuidelines: Boolean = true,
    val lineWidth: Float = 5F,
    @ColorRes
    val lineColor: Int = android.R.color.black,
    val guidelineWidth: Float = 3F,
    @ColorRes
    val guidelineColor: Int = android.R.color.darker_gray,
    val nodesMode: NodesMode = NodesMode.ALL,
    val nodeRadiusPx: Float = 14F,
    @ColorRes
    val nodeFillColor: Int = android.R.color.white,
    val valueScaleWidthPx: Float = 100F,
    val valueTextSize: Float = 36F,
    @ColorRes
    val valueTextColor: Int = android.R.color.black,
    val weekdayStart: Int = Calendar.MONDAY,
    val weekdayNameMap: Map<Int, String> = emptyMap(),
    val weekdayScaleHeightPx: Float = 70F,
    val weekdayTextSize: Float = 36F,
    @ColorRes
    val weekdayTextColor: Int = android.R.color.black
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

    fun setup(params: Params) {
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
            drawScaleWeekdays(canvas)
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
            path.lineTo(x, height - params.weekdayScaleHeightPx)
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
                canvas.drawPath(path, getLinePaint())
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