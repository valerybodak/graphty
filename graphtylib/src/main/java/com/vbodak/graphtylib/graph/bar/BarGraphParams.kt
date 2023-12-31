package com.vbodak.graphtylib.graph.bar

import com.vbodak.graphtylib.graph.base.BaseGraphParams

class BarGraphParams : BaseGraphParams() {
    var barColors: List<Int> = listOf(
        android.R.color.holo_green_dark,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark
    )
    var barWidthPx: Float = 40F
    var barCornerRadiusPx: Float = 0F
}