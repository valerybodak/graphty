package com.vbodak.graphty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vbodak.graphty.databinding.ActivityMainBinding
import com.vbodak.graphtylib.graph.week.Params
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGraph()
        displayGraph()
    }

    private fun setupGraph() {
        binding.weekLineGraph.setup(
            params = Params(
                minValue = 10,
                maxValue = 100,
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

    private fun displayGraph(){
        binding.weekLineGraph.draw(
            values = getValues()
        )
    }

    private fun getValues(): List<Int>{
        return listOf(10, 55, 25, 40, 45, 100, 78)
    }
}