package com.example.qiuhao_zheng_myruns5.history

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.qiuhao_zheng_myruns5.R
import com.example.qiuhao_zheng_myruns5.databases.HealthData

class MyListAdapter(private val context: Context, private var healthDataList: List<HealthData>) : BaseAdapter() {

    override fun getItem(position: Int): Any {
        return healthDataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return healthDataList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.data_item, null)
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val setUnitPrefer = sharedPreferences.getString("unitPrefer", "")

        val inputTypeValue : TextView = view.findViewById(R.id.inputTypeValue)
        val typeValue : TextView = view.findViewById(R.id.typeValue)
        val dateValue : TextView = view.findViewById(R.id.dateValue)
        val distanceValue : TextView = view.findViewById(R.id.distanceValue)
        val unitValue : TextView = view.findViewById(R.id.unitValue)
        val durationValue : TextView = view.findViewById(R.id.durationValue)

        val healthData = healthDataList[position]
//        dateValue.text = healthData.id.toString() // test use only
        inputTypeValue.text = healthData.inputType
        typeValue.text = healthData.type
        dateValue.text = healthData.date
//        distanceValue.text = healthData.distance
//        when (setUnitPrefer) {
//            "Meter" -> unitValue.text = "Kilometers"
//            else -> unitValue.text = "Miles"
//        }
        when (setUnitPrefer) {
            "Meter" -> {
                if (healthData.distance == "0") {
                    distanceValue.text = healthData.distance
                } else {
                    distanceValue.text =
                        String.format("%.2f", healthData.distance.toDouble() * 1.609344)
                }
                unitValue.text = "Kilometers"
            }
            else -> {
                if (healthData.distance == "0") {
                    distanceValue.text = healthData.distance
                } else {
                    distanceValue.text = String.format("%.2f", healthData.distance.toDouble())
                }
                unitValue.text = "Miles"
            }
        }
//        unitValue.text = "Kilometers" // update it with setting - unit preference
        durationValue.text = healthData.duration


        view.setOnClickListener {
            if (healthData.inputType == "Manual Entry") {
                val intent = Intent(context, RecordDet::class.java).apply {
                    putExtra("record_id", healthData.id)
                    putExtra("type", healthData.type)
                    putExtra("date", healthData.date)
                    putExtra("duration", healthData.duration)
                    putExtra("distance", healthData.distance)
                    putExtra("calories", healthData.calories)
                    putExtra("heartRate", healthData.heartRate)
                    putExtra("unitPrefer", unitValue.text)
                }
                context.startActivity(intent)
            } else if (healthData.inputType == "GPS" || healthData.inputType == "Automatic") {
//                println("qz: gps test");
                val intents = Intent(context, GpsRecord::class.java).apply {
                    putExtra("record_id", healthData.id)
                    putExtra("type", healthData.type)
//                    putExtra("date", healthData.date)
//                    putExtra("duration", healthData.duration)
                    putExtra("distance", healthData.distance)
                    putExtra("calories", healthData.calories)
                    putExtra("avgSpeed", healthData.avgSpeedDB)
                    putExtra("climb", healthData.climb)
//                    putExtra("heartRate", healthData.heartRate)
                    putExtra("unitPrefer", unitValue.text)
                    putExtra("startPTLat", healthData.startPTLat)
                    putExtra("startPTLng", healthData.startPTLng)
                    putExtra("endPTLat", healthData.endPTLat)
                    putExtra("endPTLng", healthData.endPTLng)
                    putExtra("path", healthData.path)
                }
                context.startActivity(intents)
            }
        }

        return view
    }

    fun replace(newHealthDataList: List<HealthData>) {
        healthDataList = newHealthDataList
//        notifyDataSetChanged()
    }
}