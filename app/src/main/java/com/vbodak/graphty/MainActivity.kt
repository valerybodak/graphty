package com.vbodak.graphty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vbodak.graphty.databinding.ActivityMainBinding
import com.vbodak.graphtylib.graph.bar.Bar
import com.vbodak.graphtylib.graph.bar.BarGraphParams
import com.vbodak.graphtylib.graph.week.WeekLineGraphParams
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGraph1()
        displayGraph1()

        setupGraph2()
        displayGraph2()
    }

    private fun setupGraph1() {
        val params = BarGraphParams()
        params.minValue = 5
        params.maxValue = 100
        params.valueScaleWidthPx = 82F
        params.titleTextSize = 30F
        params.barColors = listOf(R.color.cyan, R.color.pink, R.color.yellow_2)
        params.barCornerRadiusPx = 6F
        binding.barGraph.setup(
            params = params
        )
    }

    private fun displayGraph1() {
        binding.barGraph.draw(
            bars = getBars1()
        )
    }

    private fun getBars1(): List<Bar> {
        return listOf(
            Bar(title = "12/10", values = listOf(80)),
            Bar(title = "13/10", values = listOf(65)),
            Bar(title = "14/10", values = listOf(69, 32, 15)),
            Bar(title = "15/10", values = listOf(46, 15, 23)),
            Bar(title = "16/10", values = listOf(96)),
            Bar(title = "17/10", values = listOf(78)),
            Bar(title = "18/10", values = listOf(70, 60, 43))
        )
    }

    private fun setupGraph2() {
        val params = WeekLineGraphParams()
        params.minValue = 5
        params.maxValue = 100
        params.valueScaleWidthPx = 82F
        params.titleTextSize = 30F
        params.weekdayStart = Calendar.SUNDAY
        params.weekdayNameMap = mapOf(
            Calendar.SUNDAY to "S",
            Calendar.MONDAY to "M",
            Calendar.TUESDAY to "T",
            Calendar.WEDNESDAY to "W",
            Calendar.THURSDAY to "T",
            Calendar.FRIDAY to "F",
            Calendar.SATURDAY to "S"
        )

        binding.weekGraph.setup(
            params = params
        )
    }

    private fun displayGraph2() {
        binding.weekGraph.draw(
            values = listOf<Int>(32, 176, 33, 568, 7, 65, 43, 56)
        )
    }
}