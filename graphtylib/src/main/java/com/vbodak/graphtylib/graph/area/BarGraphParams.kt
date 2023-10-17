package com.vbodak.graphtylib.graph.area

import androidx.annotation.ColorRes
import com.vbodak.graphtylib.common.CommonConst
import java.util.*

data class BarGraphParams(
    /**
     * Min value to display on scale
     */
    val minValue: Int = CommonConst.UNDEFINED,
    /**
     * Max value to display on scale
     */
    val maxValue: Int = CommonConst.UNDEFINED,
    val valueScaleWidthPx: Float = 100F,
    val valueTextSize: Float = 36F,
    @ColorRes
    val valueTextColor: Int = android.R.color.black,
    val weekdayStart: Int = Calendar.MONDAY,
    val weekdayNameMap: Map<Int, String> = emptyMap(),
    val weekdayScaleHeightPx: Float = 70F,
    val weekdayTextSize: Float = 36F,
    @ColorRes
    val weekdayTextColor: Int = android.R.color.black,
    @ColorRes
    val barColors: List<Int> = listOf(android.R.color.holo_green_dark, android.R.color.holo_red_light, android.R.color.holo_blue_dark),
    val barWidthPx: Float = 40F
)