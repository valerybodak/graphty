package com.vbodak.graphtylib.graph.week

import androidx.annotation.ColorRes
import com.vbodak.graphtylib.common.CommonConst
import com.vbodak.graphtylib.graph.base.BaseGraphParams
import java.util.*

class WeekLineGraphParams : BaseGraphParams() {
    var enableGuidelines: Boolean = true
    var lineWidth: Float = 5F

    @ColorRes
    var lineColor: Int = android.R.color.black
    var guidelineWidth: Float = 3F

    @ColorRes
    var guidelineColor: Int = android.R.color.darker_gray
    var nodesMode: NodesMode = NodesMode.ALL
    var nodeRadiusPx: Float = 14F

    @ColorRes
    var nodeFillColor: Int = android.R.color.white
    var weekdayStart: Int = Calendar.MONDAY
    var weekdayNameMap: Map<Int, String> = emptyMap()
}