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
                startWeekday = Calendar.MONDAY
            ),
            values = getValues()
        )
    }

    private fun getValues(): List<Int>{
        return listOf(1, 46, 21, 60, 51, 99, 8)
    }
}