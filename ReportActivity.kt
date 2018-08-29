package com.example.diplomat.wajure

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate




class ReportActivity : Activity(){

    lateinit var chart: BarChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_activity)
        // Initialize bar chart
        val barChart = findViewById<BarChart>(R.id.barChart)
        // Create bars
        val yvalues = ArrayList<BarEntry>()
        yvalues.add(BarEntry(0f, 0f))
        yvalues.add(BarEntry(1f, 1f))
        // Create a data set
        val dataSet = BarDataSet(yvalues, "Tenses")
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
        xVals.add("Simple Past")
        xVals.add("Past Perfect")
        xVals.add("Conditional")
        xVals.add("Cond. Perfect")
        xVals.add("Future")

        // Display labels for bars
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xVals)

        // Set the maximum value that can be taken by the bars
        barChart.axisLeft.axisMaximum = 100f

        // Bars are sliding in from left to right
        barChart.animateXY(1000, 1000)
        // Display scores inside the bars
        barChart.setDrawValueAboveBar(false)

        // Hide grid lines
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        // Hide graph description
        barChart.description.isEnabled = false
        // Hide graph legend
        barChart.legend.isEnabled = false

        // Design
        dataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        data.setValueTextSize(13f)
        data.setValueTextColor(Color.DKGRAY)

        barChart.invalidate()

    }


}
