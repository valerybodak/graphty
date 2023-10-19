package com.vbodak.graphtylib.graph.week

import androidx.annotation.ColorRes
import com.vbodak.graphtylib.common.CommonConst
import java.util.*

data class WeekLineGraphParams(
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