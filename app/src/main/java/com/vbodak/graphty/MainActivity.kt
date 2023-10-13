package com.vbodak.graphty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vbodak.graphty.databinding.ActivityMainBinding
import com.vbodak.graphtylib.graph.week.WeekLineGraph
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGraph()
    }

    private fun setupGraph() {
        binding.weekLineGraph.displayValues(
            params = WeekLineGraph.Params(
                minValue = 0,
                maxValue = 100,
                weekdayStart = Calendar.MONDAY,
                weekdayNameMap = mapOf(
                    Calendar.MONDAY to "M",
                    Calendar.TUESDAY to "T",
                    Calendar.WEDNESDAY to "W",
                    Calendar.THURSDAY to "T",
                    Calendar.FRIDAY to "F",
                    Calendar.SATURDAY to "S",
                    Calendar.SUNDAY to "S",
                )
            ),
            values = getValues()
        )
    }

    private fun getValues(): List<Int>{
        return listOf(50, 0, 50, 51, 18, 0, 8)
    }
}