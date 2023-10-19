package com.vbodak.graphty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vbodak.graphty.databinding.ActivityMainBinding
import com.vbodak.graphtylib.graph.area.Bar
import com.vbodak.graphtylib.graph.area.BarGraphParams

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
        binding.barGraph1.setup(
            params = params
        )
    }

    private fun displayGraph1() {
        binding.barGraph1.draw(
            bars = getBars1()
        )
    }

    private fun getBars1(): List<Bar> {
        return listOf(Bar(title = "12/10", values = listOf(80)),
            Bar(title = "13/10", values = listOf(65)),
            Bar(title = "14/10", values = listOf(69, 32, 15)),
            Bar(title = "15/10", values = listOf(46, 15, 23)),
            Bar(title = "16/10", values = listOf(96)),
            Bar(title = "17/10", values = listOf(78)),
            Bar(title = "18/10", values = listOf(70, 60, 43)))
    }

    private fun setupGraph2() {
        val params = BarGraphParams()
        params.minValue = 5
        params.maxValue = 100
        params.valueScaleWidthPx = 82F
        params.titleTextSize = 30F
        params.barColors = listOf(R.color.cyan, R.color.pink, R.color.yellow_2)
        params.barCornerRadiusPx = 6F

        binding.barGraph2.setup(
            params = params
        )
    }

    private fun displayGraph2() {
        binding.barGraph2.draw(
            bars = getBars2()
        )
    }

    private fun getBars2(): List<Bar> {
        return listOf(Bar(title = "12/10", values = listOf(80, 61)),
            Bar(title = "13/10", values = listOf(65, 32)),
            Bar(title = "14/10", values = listOf(65, 32)),
            Bar(title = "15/10", values = listOf(32, 21)),
            Bar(title = "16/10", values = listOf(96, 76)),
            Bar(title = "17/10", values = listOf(78, 60)),
            Bar(title = "18/10", values = listOf(20, 10)))
    }
}