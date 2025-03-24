package com.example.qiuhao_zheng_myruns5.history

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.qiuhao_zheng_myruns5.R
import com.example.qiuhao_zheng_myruns5.R.*
import com.example.qiuhao_zheng_myruns5.databases.DataViewModelFactory
import com.example.qiuhao_zheng_myruns5.databases.DatabaseRepository
import com.example.qiuhao_zheng_myruns5.databases.HealthDataViewModel
import com.example.qiuhao_zheng_myruns5.databases.HealthDatabase

class RecordDet : AppCompatActivity() {
    private lateinit var database: HealthDatabase
    private lateinit var repository: DatabaseRepository
    private lateinit var viewModelFactory: DataViewModelFactory
    private lateinit var viewModel: HealthDataViewModel
    private var currentLine: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.record)

        val recordID = intent.getLongExtra("record_id", -1)
        val type = intent.getStringExtra("type") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val duration = intent.getStringExtra("duration") ?: ""
        val distance = intent.getStringExtra("distance") ?: ""
        val calories = intent.getStringExtra("calories") ?: ""
        val heartRate = intent.getStringExtra("heartRate") ?: ""
        val unitPrefer = intent.getStringExtra("unitPrefer") ?: ""

        // lines
        val line1 = findViewById<View>(R.id.sepLine1)
        val line2 = findViewById<View>(R.id.sepLine2)
        val line3 = findViewById<View>(R.id.sepLine3)
        val line4 = findViewById<View>(R.id.sepLine4)
        val line5 = findViewById<View>(R.id.sepLine5)
        val line6 = findViewById<View>(R.id.sepLine6)
        val line7 = findViewById<View>(R.id.sepLine7)

        val inputTypeTextView = findViewById<TextView>(id.inputType)
        val typeTextView = findViewById<TextView>(id.type)
        typeTextView.text = type
        val dateTextView = findViewById<TextView>(id.dateTime)
        dateTextView.text = date
        val durationTextView = findViewById<TextView>(id.duration)
        durationTextView.text = duration
        val distanceTextView = findViewById<TextView>(id.distance)
        when (unitPrefer) {
            "Kilometers" -> {
                if (distance == "0") {
                    distanceTextView.text = "0 Kilometers"
                } else {
                    distanceTextView.text = String.format("%.2f", distance.toDouble() * 1.609344) + " Kilometers"
                }
            }
            else -> {
                if (distance == "0") {
                    distanceTextView.text = "0 Miles"
                } else {
                    distanceTextView.text = distance + " Miles"
                }
            }
        }
//        distanceTextView.text = distance
        val caloriesTextView = findViewById<TextView>(id.calories)
        caloriesTextView.text = calories
        val heartRateTextView = findViewById<TextView>(id.heartRate)
        heartRateTextView.text = heartRate


        database = HealthDatabase.getInstance(this)
        repository = DatabaseRepository(database.databaseDao)
        viewModelFactory = DataViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HealthDataViewModel::class.java]

        // listen on textview to update line color
        inputTypeTextView.setOnClickListener {
            currentLine?.setBackgroundColor(Color.DKGRAY)
            line1.setBackgroundColor(Color.parseColor("#4175E4"))
            currentLine = line1
        }

        typeTextView.setOnClickListener {
            currentLine?.setBackgroundColor(Color.DKGRAY)
            line2.setBackgroundColor(Color.parseColor("#4175E4"))
            currentLine = line2
        }

        dateTextView.setOnClickListener {
            currentLine?.setBackgroundColor(Color.DKGRAY)
            line3.setBackgroundColor(Color.parseColor("#4175E4"))
            currentLine = line3
        }

        durationTextView.setOnClickListener {
            currentLine?.setBackgroundColor(Color.DKGRAY)
            line4.setBackgroundColor(Color.parseColor("#4175E4"))
            currentLine = line4
        }

        distanceTextView.setOnClickListener {
            currentLine?.setBackgroundColor(Color.DKGRAY)
            line5.setBackgroundColor(Color.parseColor("#4175E4"))
            currentLine = line5
        }

        caloriesTextView.setOnClickListener {
            currentLine?.setBackgroundColor(Color.DKGRAY)
            line6.setBackgroundColor(Color.parseColor("#4175E4"))
            currentLine = line6
        }

        heartRateTextView.setOnClickListener {
            currentLine?.setBackgroundColor(Color.DKGRAY)
            line7.setBackgroundColor(Color.parseColor("#4175E4"))
            currentLine = line7
        }


        findViewById<Button>(id.buttonDelete).setOnClickListener {
            viewModel.deleteById(recordID)
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}