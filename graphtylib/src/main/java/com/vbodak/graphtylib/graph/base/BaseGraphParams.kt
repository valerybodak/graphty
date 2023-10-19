package com.vbodak.graphtylib.graph.base

import androidx.annotation.ColorRes
import com.vbodak.graphtylib.common.CommonConst

open class BaseGraphParams {
    /**
     * Min value to display on scale
     */
    var minValue: Int = CommonConst.UNDEFINED

    /**
     * Max value to display on scale
     */
    var maxValue: Int = CommonConst.UNDEFINED

    var valueScaleWidthPx: Float = 100F
    var valueTextSize: Float = 36F

    @ColorRes
    var valueTextColor: Int = android.R.color.black
    var titleScaleHeightPx: Float = 70F
    var titleTextSize: Float = 36F

    @ColorRes
    var titleTextColor: Int = android.R.color.black
}