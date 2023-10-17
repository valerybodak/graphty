package com.vbodak.graphtylib.graph.area

import androidx.annotation.ColorRes

data class Area(
    val lineWidth: Float = 5F,
    @ColorRes
    val lineColor: Int = android.R.color.black,
    @ColorRes
    val backgroundColor: Int = android.R.color.white,
    val values: List<Pair<String, Int>> = emptyList()
)