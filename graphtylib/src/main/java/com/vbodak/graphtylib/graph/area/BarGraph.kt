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

    private val paint: Paint by lazy {
        val paint = Paint()
        paint.style = Paint.Style.FILL
        //paint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), getColor(color), getColor(color), Shader.TileMode.MIRROR)
        //paint.strokeWidth = 6F
        paint
    }

    private var bars: List<Bar> = emptyList()
    private var params: BarGraphParams = BarGraphParams()

    fun setup(params: BarGraphParams) {
        this.params = params
    }

    fun draw(areas: List<Bar>) {
        this.bars = areas
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (bars.isNotEmpty()) {
            for(area in bars){
                drawArea(canvas, area)
            }
            drawScaleValues(canvas)
            drawScaleTitles(canvas)
        }
    }

    private fun getGraphPoints(area: Bar, pointCallback: (index: Int, value: Int, x: Float, y: Float) -> Unit) {
        /*val divisionWidth = getVerticalDivisionWidth()
        for (index in area.values.indices) {
            val value = area.values[index]
            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx
            val currentX = divisionLeft + (divisionWidth / 2F)
            val currentY = getPointY(value.second)
            pointCallback.invoke(index, value.second, currentX, currentY)
        }*/
    }

    private fun drawArea(canvas: Canvas, area: Bar){
        val path = Path()
        getGraphPoints(area) { index, _, x, y ->
            if (index == 0) {
                path.moveTo(x, getGraphBottom())
                path.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                if(index == area.values.lastIndex){
                    path.lineTo(x, getGraphBottom())
                    //path.close()
                }
                //val paint = Paint()
               //paint.style = Paint.Style.FILL_AND_STROKE
                //paint.setAntiAlias(true);
                //paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.ADD));
                //paint.color = getColor(area.backgroundColor)
                //paint.setAntiAlias(true);
                //paint.setXfermode( PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                //paint.shader = LinearGradient(0f, 0f, 0f, getGraphBottom(), getColor(area.backgroundColor), Color.WHITE, Shader.TileMode.MIRROR)
                /*paint.shader = LinearGradient(
                    0, 0, 100, 100, Color.argb(50F, 23F, 65F, 14F),
                    Color.argb(50F, 3F, 67F, 78F), Shader.TileMode.REPEAT
                )*/
                //paint.alpha = 0x80
                //canvas.save()
                canvas.drawPath(path, paint)
                //canvas.save()
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

    private fun drawScaleTitles(canvas: Canvas) {
        val divisionWidth = getVerticalDivisionWidth()
        for (index in bars.indices) {
            val divisionLeft = index * divisionWidth + params.valueScaleWidthPx

            val weekdayPaint = getWeekdayPaint()
            val textBoundWeekday = Rect()
            val valueTitle = bars[index].title
            weekdayPaint.getTextBounds(valueTitle, 0, valueTitle.length, textBoundWeekday)

            val weekdayTop = height - (params.weekdayScaleHeightPx / 2) + (textBoundWeekday.height() / 2)

            val weekdayLeft =
                divisionLeft + (divisionWidth / 2) - (textBoundWeekday.width() / 2)

            canvas.drawText(
                valueTitle,
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

    private fun getVerticalDivisionWidth(): Float {
        return (width - params.valueScaleWidthPx) / bars.size
    }

    private fun getValueScaleTextHeight(): Int {
        //It can be any value because we use it only to measure text's container height
        val title = "0"
        val textPaint = getValueTextPaint()
        val textBound = Rect()
        textPaint.getTextBounds(title, 0, title.length, textBound)
        return textBound.height()
    }

    /*private fun getAreaPaint(@ColorRes color: Int): Paint {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), getColor(color), Color., Shader.TileMode.MIRROR)
        paint.strokeWidth = 6F
        paint.color = getColor(color)
        return paint
    }*/

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
        return height - params.weekdayScaleHeightPx - (getValueScaleTextHeight() / 2F)
    }

    private fun getColor(@ColorRes colorResource: Int): Int {
        return ContextCompat.getColor(context, colorResource)
    }
}