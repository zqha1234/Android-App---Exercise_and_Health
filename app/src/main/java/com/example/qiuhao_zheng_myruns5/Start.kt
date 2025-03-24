package com.example.qiuhao_zheng_myruns5

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner


class Start : Fragment() {
    private lateinit var inputType: Spinner
    private lateinit var activityType: Spinner
    private lateinit var buttonStart: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputType = view.findViewById(R.id.inputTypeOptions)
        activityType = view.findViewById(R.id.activityTypeOptions)
        buttonStart = view.findViewById(R.id.buttonStart)

        val inputTypeOptions = arrayOf("Manual Entry", "GPS", "Automatic")
        val activityTypeOptions = arrayOf("Running", "Walking", "Standing", "Cycling", "Hiking",
            "Downhill Skiing", "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
            "Mountain Biking", "Wheelchair", "Elliptical", "Other")

        inputType.adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, inputTypeOptions)
        activityType.adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, activityTypeOptions)



        // start button listener
        buttonStart.setOnClickListener {
            val inputTypeChosen = inputType.selectedItem
            val activityTypeChosen = activityType.selectedItem
//            println("qz: $inputTypeChosen $activityTypeChosen") // test use only

//            val intentSer = Intent(requireContext(), TrackingService::class.java)
//            requireContext().startService(intentSer)

            when (inputTypeChosen) {
                "GPS" -> {
                    Thread {
                        val serviceIntent = Intent(requireContext(), TrackingService::class.java)
                        if (Build.VERSION.SDK_INT >= 26) {
                            requireContext().startForegroundService(serviceIntent)
                        } else {
                            requireContext().startService(serviceIntent)
                        }
                    }.start()
                }
                "Automatic" -> {
                    Thread {
                        val serviceIntent = Intent(requireContext(), AutomaticTrackingSer::class.java)
                        if (Build.VERSION.SDK_INT >= 26) {
                            requireContext().startForegroundService(serviceIntent)
                        } else {
                            requireContext().startService(serviceIntent)
                        }
                    }.start()
                }
            }

            val intent = when (inputTypeChosen) {
                "Manual Entry" -> Intent(context, MenuEntryFragment::class.java)
                "GPS" -> {
                    Intent(context, GPS::class.java)
                }
                "Automatic" -> Intent(context, Automatic::class.java)
                else -> null
            }
            intent?.putExtra("type", activityTypeChosen as? String ?: "null")
            intent?.let { startActivity(it) }

        }
    }

//    private fun replaceFragment(newFragment: Fragment) {
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.activity_main, newFragment)
//            .addToBackStack(null)
//            .commit()
//    }

}