package com.example.washingmachinewatcher.ui.fragments

import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.washingmachinewatcher.databinding.FragmentAdvancedBinding
import com.example.washingmachinewatcher.network.SensorData
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.*
import kotlin.concurrent.thread

class AdvancedFragment : Fragment() {

    private lateinit var binding: FragmentAdvancedBinding
    private val sensorData = SensorData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAdvancedBinding.inflate(layoutInflater)

        styleChart()

        thread {
            setData()
            showChart()
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                setData()
            }
        }, 5000)


        return binding.root
    }

    private fun styleChart() {
        binding.chart.description.isEnabled = false
        binding.chart.setTouchEnabled(true)
        binding.chart.setDrawGridBackground(false)
        binding.chart.isDragEnabled = true
        binding.chart.setScaleEnabled(true)
        binding.chart.setPinchZoom(true)

        val xAxis = binding.chart.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val yAxis = binding.chart.axisLeft
        binding.chart.axisRight.isEnabled = false
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f

        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)
    }

    private fun showChart() {
        this.activity?.runOnUiThread{
            binding.chart.animateX(500)
            val legend = binding.chart.legend
            legend.form = Legend.LegendForm.LINE
        }
    }

    private fun getData(): ArrayList<Entry> {
        val values = ArrayList<Entry>()
        val result = sensorData.getDataWithQuery("{\"query\": \"SELECT \$ts as ts, sensors.battery as data FROM dtmi:pa76fskub:m5mtazvjfgv WHERE WITHIN_WINDOW(P1D)\"}")
        if (result != null) {
            var i = 0.0F
            for (data in result.results) {
                values.add(Entry(i, data.data.toFloat()))
                i++
            }
        }
        return values
    }

    private fun setData() {
        val values = getData()
        this.activity?.runOnUiThread {
            val set1: LineDataSet
            if (binding.chart.data != null && binding.chart.data.dataSetCount > 0) {
                set1 = binding.chart.data.getDataSetByIndex(0) as LineDataSet
                set1.values = values
                set1.notifyDataSetChanged()
                binding.chart.data.notifyDataChanged()
                binding.chart.notifyDataSetChanged()
            } else {
                set1 = LineDataSet(values, "Sensor Data")
                set1.setDrawIcons(false)
                set1.enableDashedLine(10f, 5f, 0f)
                set1.lineWidth = 1f
                set1.circleRadius = 3f
                set1.setDrawCircleHole(false)
                set1.formLineWidth = 1f
                set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                set1.formSize = 15f
                set1.valueTextSize = 9f
                set1.enableDashedHighlightLine(10f, 5f, 0f)
                set1.setDrawFilled(true)
                set1.fillFormatter = IFillFormatter { _, _ -> binding.chart.axisLeft.axisMinimum }
                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(set1)
                val data = LineData(dataSets)
                binding.chart.data = data
            }
        }
    }

}