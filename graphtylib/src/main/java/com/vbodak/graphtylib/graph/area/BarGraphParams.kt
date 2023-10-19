package com.vbodak.graphtylib.graph.area

import androidx.annotation.ColorRes
import com.vbodak.graphtylib.common.CommonConst
import com.vbodak.graphtylib.graph.base.BaseParams

class BarGraphParams : BaseParams() {
    @ColorRes
    var barColors: List<Int> = listOf(
        android.R.color.holo_green_dark,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark
    )
    var barWidthPx: Float = 40F
    var barCornerRadiusPx: Float = 0F
}