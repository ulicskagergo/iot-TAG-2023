package com.example.washingmachinewatcher.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.washingmachinewatcher.databinding.FragmentSimpleBinding
import com.example.washingmachinewatcher.network.SensorData
import java.util.*
import kotlin.concurrent.thread

class SimpleFragment : Fragment() {
    private lateinit var binding: FragmentSimpleBinding
    private val sensorData = SensorData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSimpleBinding.inflate(layoutInflater)

        thread {
            retrieveData()
        }
        scheduleUpdater()

        return binding.root
    }

    private fun scheduleUpdater() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                retrieveData()
            }
        }, 5000)
    }

    fun retrieveData() {
        val result =
            sensorData.getDataWithQuery("{\"query\": \"SELECT \$ts as ts, sensors.battery as data FROM dtmi:pa76fskub:m5mtazvjfgv WHERE WITHIN_WINDOW(P1D)\"}")
        if (result != null) {
            val data = result.results.last().data
            val prediction: RemainingTime = if (data < 0) {
                RemainingTime.UNPRED
            } else if (data < 5) {
                RemainingTime.NO
            } else if (data < 20) {
                RemainingTime.LITTLE
            } else {
                RemainingTime.LOT
            }
            update("${prediction.time}\n(Amplitude is ${data}).")
        }
    }

    private fun update(value: String) {
        this.activity?.runOnUiThread {
            binding.textView.text = value
        }
    }

    enum class RemainingTime(val time: String) {
        LOT("There is a lot of time left"),
        LITTLE("There is a little of time left"),
        NO("There is no time left"),
        UNPRED("Remaining time cannot be predicted")
    }
}