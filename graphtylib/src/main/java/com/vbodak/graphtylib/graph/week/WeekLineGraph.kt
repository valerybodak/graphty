package com.vbodak.graphtylib.graph.week

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.util.Calendar

class WeekLineGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val UNDEFINED = -1F
        private const val WEEKDAYS_NUMBER = 7
    }

    data class Params(
        val startWeekday: Int = Calendar.MONDAY,
        val weekdayNameMap: Map<Int, String> = emptyMap(),
        val weekdaysHeightPx: Float = 60F,
        val weekdaysTextSize: Float = 40F,
        @ColorRes
        val weekdaysTextColor: Int = android.R.color.black
    )

    private var params: Params = Params()
    private var values: List<Int> = emptyList()

    fun displayValues(params: Params, values: List<Int>) {
        this.params = params
        this.values = values
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (values.isNotEmpty()) {
            drawLine(canvas)
            drawWeekdays(canvas)

            /*drawDivisions(canvas)
            drawDottedLines(canvas)
            drawPressureGraph(canvas)
            drawPulseGraph(canvas)
            drawPulseGraphNodes(canvas)
            drawDates(canvas)*/
        }
    }

    private fun drawLine(canvas: Canvas) {

        val linePaint = getLinePaint()
        val MAX_SCALE_VALUE = 100F

        var prevX = UNDEFINED
        var prevY = UNDEFINED
        val divisionWidth = width / WEEKDAYS_NUMBER
        //val lineStrokeWidth = 20
        //val lineStrokeWidth = dpToPx(R.dimen.dual_graph_pulse_line_stroke_width)
        for (index in values.indices) {
            val item = values[index]
            //val pulseValue = item.pulse

            val divisionLeft = index * divisionWidth

            val currentX =
                divisionLeft + (divisionWidth / 2F)

            //var currentY = getGraphTop()
            var currentY = getGraphTop()
            if (item > 0 && item < MAX_SCALE_VALUE) {
                currentY =
                    getGraphBottom() - (getGraphHeight() / (MAX_SCALE_VALUE / item.toFloat()))
            }

            if (prevX == UNDEFINED && prevY == UNDEFINED) {
                //the first point
                prevX = currentX
                prevY = currentY
            } else {
                //draw line without gaps
                canvas.drawLine(prevX, prevY, currentX, currentY, linePaint)

                prevX = currentX
                prevY = currentY
            }

        }

    }

    private fun drawWeekdays(canvas: Canvas) {
        val divisionWidth = width / WEEKDAYS_NUMBER
        val weekdayTitles = getWeekdayTitleList()
        for (index in values.indices) {
            val item = values[index]

            val divisionLeft = index * divisionWidth

            if (true) {
                val textWeekday = getWeekdayPaint()

                val textBoundWeekday = Rect()
                val weekdayTitle = weekdayTitles.get(index)
                textWeekday.getTextBounds(weekdayTitle, 0, weekdayTitle.length, textBoundWeekday)

                val weekdayTop = height - (params.weekdaysHeightPx / 2) + (textBoundWeekday.height() / 2)

                val weekdayLeft =
                    divisionLeft + (divisionWidth / 2) - (textBoundWeekday.width() / 2)

                canvas.drawText(
                    weekdayTitle,
                    weekdayLeft.toFloat(),
                    weekdayTop,
                    textWeekday
                )
            }
        }
    }

    private fun getWeekdayTitleList(): List<String> {
        val titles = mutableListOf<String>()
        val currentDay = Calendar.getInstance()
        while (titles.size != WEEKDAYS_NUMBER) {
            currentDay.add(Calendar.DAY_OF_WEEK, 1)
            val weekday = currentDay.get(Calendar.DAY_OF_WEEK)
            if (weekday == params.startWeekday) {
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

    private fun getWeekdayPaint(): Paint {
        val paint = TextPaint()
        paint.isAntiAlias = true
        paint.textSize = params.weekdaysTextSize
        paint.color = getColor(params.weekdaysTextColor)
        return paint
    }

    private fun getGraphHeight(): Float {
        return getGraphBottom() - getGraphTop()
    }

    //**
    // Graph's top is the first top dotted line (it is not(!) top of view)
    private fun getGraphTop(): Float {
        return (getScaleValueTextHeight(context) / 2F)
    }

    //**
    // Graph's bottom is the last top dotted line (it is not(!) bottom of view)
    private fun getGraphBottom(): Float {
        return height - params.weekdaysHeightPx - (getScaleValueTextHeight(context) / 2F)
    }

    fun getScaleValueTextHeight(context: Context): Int {
        //It can be any value because we use it only to measure text's container height
        val title = "0"
        val textPaint = getScaleValueTextPaint(context)

        val textBound = Rect()
        textPaint.getTextBounds(title, 0, title.length, textBound)
        return textBound.height()
    }

    private fun getScaleValueTextPaint(context: Context): TextPaint {
        //val customFont = ResourcesCompat.getFont(context, R.font.pt_sans_regular)
        val textPaint = TextPaint()
        //textPaint.typeface = customFont
        textPaint.isAntiAlias = true
        //textPaint.textSize = spToPx(context, R.dimen.dual_graph_scale_values_text_size)
        textPaint.textSize = 12F
        textPaint.color = Color.BLACK
        return textPaint
    }

    private fun spToPx(context: Context, dimenResource: Int): Float {
        val res = context.resources
        val metrics = res.displayMetrics
        val spValue = res.getDimension(dimenResource) / metrics.density
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics)
    }

    private fun getLinePaint(): Paint {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6F
        //paint.strokeWidth = dpToPx(R.dimen.dual_graph_pulse_line_stroke_width)
        paint.color = getColor(android.R.color.holo_green_dark)
        return paint
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