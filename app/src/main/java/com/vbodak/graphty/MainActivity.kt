package com.vbodak.graphty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vbodak.graphty.databinding.ActivityMainBinding
import com.vbodak.graphtylib.graph.week.NodesMode
import com.vbodak.graphtylib.graph.week.Params
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
        binding.weekLineGraph1.setup(
            params = Params(
                minValue = 10,
                maxValue = 100,
                lineColor = android.R.color.holo_green_light,
                lineWidth= 4F,
                nodeRadiusPx = 20F,
                enableGuidelines = false,
                nodesMode = NodesMode.NONE,
                valueScaleWidthPx = 82F,
                weekdayStart = Calendar.SUNDAY,
                weekdayNameMap = mapOf(
                    Calendar.MONDAY to "M",
                    Calendar.TUESDAY to "T",
                    Calendar.WEDNESDAY to "W",
                    Calendar.THURSDAY to "T",
                    Calendar.FRIDAY to "F",
                    Calendar.SATURDAY to "S",
                    Calendar.SUNDAY to "S",
                )
            )
        )
    }

    private fun displayGraph1(){
        binding.weekLineGraph1.draw(
            values = getValues1()
        )
    }

    private fun getValues1(): List<Int>{
        return listOf(17, 55, 15, 40, 16, 19, 97)
    }

    private fun setupGraph2() {
        binding.weekLineGraph2.setup(
            params = Params(
                minValue = 10,
                maxValue = 100,
                lineColor = android.R.color.black,
                lineWidth= 4F,
                nodeRadiusPx = 24F,
                nodeFillColor = android.R.color.holo_red_light,
                valueScaleWidthPx = 82F,
                weekdayStart = Calendar.SUNDAY,
                weekdayNameMap = mapOf(
                    Calendar.MONDAY to "M",
                    Calendar.TUESDAY to "T",
                    Calendar.WEDNESDAY to "W",
                    Calendar.THURSDAY to "T",
                    Calendar.FRIDAY to "F",
                    Calendar.SATURDAY to "S",
                    Calendar.SUNDAY to "S",
                )
            )
        )
    }

    private fun displayGraph2(){
        binding.weekLineGraph2.draw(
            values = getValues2()
        )
    }

    private fun getValues2(): List<Int>{
        return listOf(20, 71, 89, 65, 93, 20, 27)
    }
}