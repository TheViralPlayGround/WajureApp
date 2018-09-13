package com.example.diplomat.wajure

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class ReportActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_activity)
        // Initialize bar chart
        val barChart = findViewById<HorizontalBarChart>(R.id.barChart)
        // Create bars

        val bun = intent.extras.getBundle("LIST")
        var ls = arrayListOf<Int>()
        var ls2 = arrayListOf<Int>()
        if (bun.getSerializable("LIST") != null){
            ls = bun.getSerializable("LIST") as ArrayList<Int>
            ls2 = bun.get("LIST2") as ArrayList<Int>
        }
        val yvalues = ArrayList<BarEntry>()

        for (entry in ls ){
            val i = ls[entry]
            val currentY = entry
            val bE = BarEntry(currentY.toFloat(), ls2[i].toFloat())
            yvalues.add(bE)
        }
        // Create a data set
        val dataSet = BarDataSet(yvalues, "Wajures")
        dataSet.setDrawValues(true)

        // Create a data object from the dataSet
        val data = BarData(dataSet)
        // Format data as percentage
        data.setValueFormatter(PercentFormatter())

        // Make the chart use the acquired data
        barChart.data = data

        // Create the labels for the bars
        val xVals = ArrayList<String>()
        xVals.add("Present")
        xVals.add("Pres. Continuous")

        // Display labels for bars
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xVals)

        // Set the maximum value that can be taken by the bars
        barChart.axisLeft.axisMaximum = 100f

        // Bars are sliding in from left to right
//        barChart.animateXY(1000, 1000)
        // Display scores inside the bars
        barChart.setDrawValueAboveBar(true)

        // Hide grid lines
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        // Hide graph description
        barChart.description.isEnabled = false
        // Hide graph legend
        barChart.legend.isEnabled = true

        // Design
        dataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
//        dataSet.setColor(R.color.colorPrimary)
        data.setValueTextSize(18f)
        data.setValueTextColor(R.color.black)

        barChart.invalidate()

    }


}
