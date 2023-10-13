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
            if(params.enableGuides){
                drawGuides(canvas)
            }
            drawLine(canvas)
            drawValues(canvas)
            drawWeekdays(canvas)
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

            val currentX =
                divisionLeft + (divisionWidth / 2F)

            var currentY = getGraphTop()
            if (item >= params.minValue && item <= params.maxValue) {
                currentY =
                    getGraphBottom() - (getGraphHeight() / (params.maxValue / item.toFloat()))
            }

            val path = Path()
            path.moveTo(currentX, currentY)
            path.lineTo(currentX, height - params.weekdayScaleHeightPx)

            val p1 = Paint(ANTI_ALIAS_FLAG)
            p1.style = Paint.Style.STROKE
            p1.strokeWidth = 3F

            val p2 = Paint(p1)
            p2.color = getColor(android.R.color.darker_gray)
            p2.pathEffect =
                DashPathEffect(floatArrayOf(5F, 5F), 0F)

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

            var currentY = getGraphTop()
            if (item >= params.minValue && item <= params.maxValue) {
                currentY =
                    getGraphBottom() - (getGraphHeight() / (params.maxValue / item.toFloat()))
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

    private fun drawValues(canvas: Canvas) {
        val minValue = if (params.minValue != CommonConst.UNDEFINED) params.minValue else values.min()
        val maxValue = if (params.maxValue != CommonConst.UNDEFINED) params.maxValue else values.max()
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

    private fun drawWeekdays(canvas: Canvas) {
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
            if (item >= params.minValue && item <= params.maxValue) {
                currentY =
                    getGraphBottom() - (getGraphHeight() / (params.maxValue / item.toFloat()))
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

    /*companion object {
        const val UNDEFINED = -1F
    }

    interface OnGraphsListener{
        fun onItemClick(item: GraphItem)
    }

    private var measureItems: List<GraphItem> = listOf()

    private var visibleAreaWidth = 0F

    private var divisionWidth = 0F

    private var pressureBarWidth = 0F
    private var pressureBarCornerRadius = 0F

    private var dottedLineStrokeWidth = 0F
    private var dottedLineDashWidth = 0F
    private var dottedLineSpaceBetweenWidth = 0F

    private var dateContainerHeight = 0F

    private var pulseLineStrokeWidth = 0F
    private var pulseNodeInnerRadius = 0F
    private var pulseNodeOuterRadius = 0F
    private var pulseNodeOuter2Radius = 0F

    private var dateTextSpacing = 0F
    private var isScrolling = false

    var listener: OnGraphsListener? = null
    var usePulseGraphGaps = true

    init {
        setOnTouchListener(this)
    }

    override fun onDraw(canvas: Canvas) {
        if (measureItems.isNotEmpty()) {

            drawDivisions(canvas)
            drawDottedLines(canvas)
            drawPressureGraph(canvas)
            drawPulseGraph(canvas)
            drawPulseGraphNodes(canvas)
            drawDates(canvas)
        }
    }

    */
    /**
     * We override onTouch() method to handle click on graph's division item
     *//*
    override fun onTouch(v: View, event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            isScrolling = false
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (!isScrolling) {
                val clickItemPosition = (event.x / divisionWidth).toInt()
                val item = measureItems[clickItemPosition]
                listener?.onItemClick(item)
            }
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            isScrolling = true
        }

        return true
    }

    fun displayItems(items: List<GraphItem>, visibleAreaWidth: Float) {
        this.measureItems = items
        this.visibleAreaWidth = visibleAreaWidth
        calculateDimensions()
        invalidate()
    }

    private fun calculateDimensions() {

        if (measureItems.size < MAX_NUMBER_DIVISIONS_ON_SCREEN) {
            divisionWidth = visibleAreaWidth / measureItems.size
        } else {
            divisionWidth = visibleAreaWidth / MAX_NUMBER_DIVISIONS_ON_SCREEN
        }

        dottedLineStrokeWidth = dpToPx(R.dimen.dual_graph_dotted_line_stroke_width)

        pressureBarWidth = dpToPx(R.dimen.dual_graph_pressure_bar_width)
        pressureBarCornerRadius = pressureBarWidth / 2

        dottedLineDashWidth = dpToPx(R.dimen.dual_graph_dotted_line_dash_width)
        dottedLineSpaceBetweenWidth = dpToPx(R.dimen.dual_graph_dotted_line_space_between_width)

        dateContainerHeight = getDateContainerHeight(context)

        pulseLineStrokeWidth = dpToPx(R.dimen.dual_graph_pulse_line_stroke_width)
        pulseNodeInnerRadius = dpToPx(R.dimen.dual_graph_pulse_node_inner_radius)
        pulseNodeOuterRadius = dpToPx(R.dimen.dual_graph_pulse_node_outer_radius)
        pulseNodeOuter2Radius = dpToPx( R.dimen.dual_graph_pulse_node_outer2_radius)

        dateTextSpacing = dpToPx(R.dimen.dual_graph_date_spacing)
    }

    private fun drawDivisions(canvas: Canvas) {
        for (index in measureItems.indices) {
            if (index % 2 != 0) {
                val itemPaint = Paint()
                itemPaint.style = Paint.Style.FILL
                itemPaint.color = getColor(R.color.dual_graph_color_division_bg)

                val divisionLeft = index * divisionWidth
                val divisionRight = divisionLeft + divisionWidth

                val rect =
                    RectF(divisionLeft, 0F, divisionRight, height.toFloat())
                canvas.drawRect(rect, itemPaint)
            }
        }
    }

    private fun drawDottedLines(canvas: Canvas) {
        val scaleDivisionHeight =
            (height - dateContainerHeight - getScaleValueTextHeight(context)) / (MAX_SCALE_VALUE / SCALE_STEP)

        var index = 0
        for (itemValue in MAX_SCALE_VALUE downTo MIN_SCALE_VALUE step SCALE_STEP) {

            val lineTop = (scaleDivisionHeight * index) + (getScaleValueTextHeight(context) / 2F)

            val path = Path()
            path.moveTo(0F, lineTop)
            path.lineTo(width.toFloat(), lineTop)

            val p1 = Paint(ANTI_ALIAS_FLAG)
            p1.style = Paint.Style.STROKE
            p1.strokeWidth = dottedLineStrokeWidth

            val p2 = Paint(p1)
            p2.color = getColor(R.color.dual_graph_color_dotted_line)
            p2.pathEffect =
                DashPathEffect(floatArrayOf(dottedLineDashWidth, dottedLineSpaceBetweenWidth), 0F)

            canvas.drawPath(path, p2)

            index++
        }
    }

    private fun drawPressureGraph(canvas: Canvas) {
        for (index in measureItems.indices) {
            val item = measureItems[index]

            val divisionLeft = index * divisionWidth

            if (item.systolic > item.diastolic) {
                drawPressureSingleBar(
                    canvas,
                    item.systolic,
                    divisionLeft,
                    R.color.dual_graph_color_bar_systolic
                )
                drawPressureSingleBar(
                    canvas,
                    item.diastolic,
                    divisionLeft,
                    R.color.dual_graph_color_bar_diastolic
                )
            } else if (item.diastolic > item.systolic) {
                drawPressureSingleBar(
                    canvas,
                    item.diastolic,
                    divisionLeft,
                    R.color.dual_graph_color_bar_diastolic
                )
                drawPressureSingleBar(
                    canvas,
                    item.systolic,
                    divisionLeft,
                    R.color.dual_graph_color_bar_systolic
                )
            } else {
                //We need to draw outer and inner bars in case of systolic==diastolic
                //Drawing outer bar
                drawPressureSingleBar(
                    canvas,
                    item.systolic,
                    divisionLeft,
                    R.color.dual_graph_color_bar_systolic
                )

                //Drawing inner bar
                drawPressureInnerBar(
                    canvas,
                    item.diastolic,
                    divisionLeft,
                    R.color.dual_graph_color_bar_diastolic
                )
            }
        }
    }

    private fun drawPressureSingleBar(
        canvas: Canvas,
        pressureValue: Int,
        divisionLeft: Float, @ColorRes colorResId: Int
    ) {
        val pressureBarLeft =
            divisionLeft + (divisionWidth / 2F) - (pressureBarWidth / 2F)
        val pressureBarRight = pressureBarLeft + pressureBarWidth

        val pressurePaint = Paint()
        pressurePaint.style = Paint.Style.FILL
        pressurePaint.color = getColor(colorResId)

        var pressureBarTop = getGraphTop()
        if (pressureValue > 0 && pressureValue < MAX_SCALE_VALUE) {
            pressureBarTop =
                getGraphBottom() - (getGraphHeight() / (MAX_SCALE_VALUE / pressureValue.toFloat()))
        }

        val rectLowPressure =
            RectF(pressureBarLeft, pressureBarTop, pressureBarRight, getGraphBottom())
        canvas.drawRoundRect(
            rectLowPressure,
            pressureBarCornerRadius,
            pressureBarCornerRadius,
            pressurePaint
        )
    }

    private fun drawPressureInnerBar(
        canvas: Canvas,
        pressureValue: Int,
        divisionLeft: Float, @ColorRes colorResId: Int
    ) {

        val pressureInnerBarWidth =
            dpToPx(R.dimen.dual_graph_pressure_inner_bar_width)
        val pressureInnerBarCornerRadius = pressureInnerBarWidth / 2F

        val pressureBarLeft =
            divisionLeft + (divisionWidth / 2F) - (pressureInnerBarWidth / 2F)
        val pressureBarRight = pressureBarLeft + pressureInnerBarWidth

        val pressurePaint = Paint()
        pressurePaint.style = Paint.Style.FILL
        pressurePaint.color = getColor(colorResId)

        val innerBarMargin = ((pressureBarWidth - pressureInnerBarWidth) / 2F)
        var pressureBarTop = getGraphTop() + + innerBarMargin
        if (pressureValue > 0 && pressureValue < MAX_SCALE_VALUE) {
            pressureBarTop =
                (getGraphBottom() - (getGraphHeight() / (MAX_SCALE_VALUE / pressureValue.toFloat()))) + innerBarMargin
        }

        val pressureBarBottom = getGraphBottom() - innerBarMargin

        val rectLowPressure =
            RectF(pressureBarLeft, pressureBarTop, pressureBarRight, pressureBarBottom)
        canvas.drawRoundRect(
            rectLowPressure,
            pressureInnerBarCornerRadius,
            pressureInnerBarCornerRadius,
            pressurePaint
        )
    }

    private fun drawPulseGraph(canvas: Canvas) {
        val pulseLinePaint = getPulseLinePaint()

        var prevX = UNDEFINED
        var prevY = UNDEFINED
        for (index in measureItems.indices) {
            val item = measureItems[index]
            val pulseValue = item.pulse

            if (item.isPulseNotEmpty()) {

                val divisionLeft = index * divisionWidth

                val currentX =
                    divisionLeft + (divisionWidth / 2F)

                var currentY = getGraphTop()
                if (pulseValue > 0 && pulseValue < MAX_SCALE_VALUE) {
                    currentY =
                        getGraphBottom() - (getGraphHeight() / (MAX_SCALE_VALUE / pulseValue.toFloat()))
                }

                if (prevX == UNDEFINED && prevY == UNDEFINED) {
                    //the first point
                    prevX = currentX
                    prevY = currentY
                } else {

                    if (usePulseGraphGaps) {
                        if (index > 0 && measureItems[index - 1].isPulseNotEmpty()) {

                            canvas.drawLine(prevX, prevY, currentX, currentY, pulseLinePaint)

                            prevX = currentX
                            prevY = currentY
                        } else {
                            prevX = UNDEFINED
                            prevY = UNDEFINED
                        }

                        val nextIndex = index + 1
                        if (nextIndex != measureItems.size && measureItems[nextIndex].isPulseNotEmpty()) {
                            prevX = currentX
                            prevY = currentY
                        }
                    } else {

                        //draw line without gaps
                        canvas.drawLine(prevX, prevY, currentX, currentY, pulseLinePaint)

                        prevX = currentX
                        prevY = currentY
                    }
                }
            }
        }
    }

    */
    /**
     * Drawing pulse line's nodes.
     * We draw nodes in separate loop because we need to draw node' circle over the drawn line
     *//*
    private fun drawPulseGraphNodes(canvas: Canvas) {
        val nodePaint = Paint(ANTI_ALIAS_FLAG)
        nodePaint.style = Paint.Style.FILL
        for (index in measureItems.indices) {
            val item = measureItems[index]
            val pulseValue = item.pulse

            if (item.isPulseNotEmpty()) {

                val divisionLeft = index * divisionWidth

                val currentX =
                    divisionLeft + (divisionWidth / 2F)

                var currentY = getGraphTop()
                if (pulseValue > 0 && pulseValue < MAX_SCALE_VALUE) {
                    currentY =
                        getGraphBottom() - (getGraphHeight() / (MAX_SCALE_VALUE / pulseValue.toFloat()))
                }

                nodePaint.color = getColor(R.color.dual_graph_color_pulse_node_outer2)
                canvas.drawCircle(currentX, currentY, pulseNodeOuter2Radius, nodePaint)

                nodePaint.color = getColor(R.color.dual_graph_color_pulse_node_outer)
                canvas.drawCircle(currentX, currentY, pulseNodeOuterRadius, nodePaint)

                nodePaint.color = getColor(R.color.dual_graph_color_pulse_node_inner)
                canvas.drawCircle(currentX, currentY, pulseNodeInnerRadius, nodePaint)
            }
        }
    }

    private fun drawDates(canvas: Canvas) {
        for (index in measureItems.indices) {
            val item = measureItems[index]

            val divisionLeft = index * divisionWidth

            if (item.dateAppearance != null) {

                //We need to draw subtitle1 firstly because we align other titles along it
                val textPaintDateSubtitle1 = getTextDateSubtitle1Paint()

                val textBoundDateSubtitle1 = Rect()
                val dateSubtitle1 = item.dateAppearance!!.subtitle1
                textPaintDateSubtitle1.getTextBounds(
                    dateSubtitle1,
                    0,
                    dateSubtitle1.length,
                    textBoundDateSubtitle1
                )

                var dateSubtitle1Top: Float
                if (item.dateAppearance!!.hasSubtitle2()) {
                    dateSubtitle1Top =
                        height - (dateContainerHeight / 2) + (textBoundDateSubtitle1.height() / 2)
                } else {
                    dateSubtitle1Top =
                        height - dateContainerHeight / 2 + textBoundDateSubtitle1.height()
                }
                val dateSubtitle1Left =
                    divisionLeft + (divisionWidth / 2) - (textBoundDateSubtitle1.width() / 2)

                canvas.drawText(
                    dateSubtitle1,
                    dateSubtitle1Left,
                    dateSubtitle1Top,
                    textPaintDateSubtitle1
                )

                //Drawing title
                val textPaintDateTitle = getTextDateTitlePaint()

                val textBoundDateTitle = Rect()
                val dateTitle = item.dateAppearance!!.title
                textPaintDateTitle.getTextBounds(dateTitle, 0, dateTitle.length, textBoundDateTitle)

                val dateTitleTop = dateSubtitle1Top - textBoundDateTitle.height() - dateTextSpacing
                val dateTitleLeft =
                    divisionLeft + (divisionWidth / 2) - (textBoundDateTitle.width() / 2)

                canvas.drawText(
                    dateTitle,
                    dateTitleLeft,
                    dateTitleTop,
                    textPaintDateTitle
                )

                //Drawing subtitle2 if need
                if (item.dateAppearance!!.hasSubtitle2()) {
                    val textPaintDateSubtitle2 = getTextDateSubtitle2Paint()

                    val textBoundDateSubtitle2 = Rect()
                    val dateSubtitle2 = item.dateAppearance!!.subtitle2!!
                    textPaintDateSubtitle2.getTextBounds(
                        dateSubtitle2,
                        0,
                        dateSubtitle2.length,
                        textBoundDateSubtitle2
                    )

                    val dateSubtitle2Top =
                        dateSubtitle1Top + textBoundDateSubtitle1.height() + dateTextSpacing
                    val dateSubtitle2Left =
                        divisionLeft + (divisionWidth / 2) - (textBoundDateSubtitle2.width() / 2)

                    canvas.drawText(
                        dateSubtitle2,
                        dateSubtitle2Left,
                        dateSubtitle2Top,
                        textPaintDateSubtitle2
                    )
                }
            }
        }
    }

    private fun getTextDateTitlePaint(): Paint {
        val customFont = ResourcesCompat.getFont(context, R.font.pt_sans_regular)
        val paint = TextPaint()
        paint.typeface = customFont
        paint.isAntiAlias = true
        paint.textSize = spToPx(R.dimen.dual_graph_text_time_size)
        paint.color = getColor(R.color.colorWineBerry)
        return paint
    }

    private fun getTextDateSubtitle1Paint(): Paint {
        val customFont = ResourcesCompat.getFont(context, R.font.pt_sans_regular)
        val paint = TextPaint()
        paint.typeface = customFont
        paint.isAntiAlias = true
        paint.textSize = spToPx(R.dimen.dual_graph_text_date_size)
        paint.color = getColor(R.color.colorDarkGray)
        return paint
    }

    private fun getTextDateSubtitle2Paint(): Paint {
        val customFont = ResourcesCompat.getFont(context, R.font.pt_sans_regular)
        val paint = TextPaint()
        paint.typeface = customFont
        paint.isAntiAlias = true
        paint.textSize = spToPx(R.dimen.dual_graph_text_year_size)
        paint.color = getColor(R.color.colorDarkGray)
        return paint
    }

    private fun getPulseLinePaint(): Paint {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = pulseLineStrokeWidth
        paint.color = getColor(R.color.dual_graph_color_pulse_line)
        return paint
    }

    private fun getGraphHeight(): Float {
        return getGraphBottom() - getGraphTop()
    }

    */
    /**
     * Graph's top is the first top dotted line (it is not(!) top of view)
     *//*
    private fun getGraphTop(): Float {
        return (getScaleValueTextHeight(context) / 2F)
    }

    */
    /**
     * Graph's bottom is the last top dotted line (it is not(!) bottom of view)
     *//*
    private fun getGraphBottom(): Float {
        return height - dateContainerHeight - (getScaleValueTextHeight(context) / 2F)
    }

    private fun dpToPx(@DimenRes dimenResource: Int): Float{
        return Utils.dpToPx(context, dimenResource)
    }

    private fun spToPx(@DimenRes dimenResource: Int): Float{
        return Utils.spToPx(context, dimenResource)
    }

    private fun getColor(@ColorRes colorResource: Int): Int{
        return ContextCompat.getColor(context, colorResource)
    }*/
}