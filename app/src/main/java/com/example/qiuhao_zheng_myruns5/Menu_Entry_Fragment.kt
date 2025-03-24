package com.example.qiuhao_zheng_myruns5

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.qiuhao_zheng_myruns5.databases.DataViewModelFactory
import com.example.qiuhao_zheng_myruns5.databases.DatabaseDao
import com.example.qiuhao_zheng_myruns5.databases.DatabaseRepository
import com.example.qiuhao_zheng_myruns5.databases.HealthData
import com.example.qiuhao_zheng_myruns5.databases.HealthDataViewModel
import com.example.qiuhao_zheng_myruns5.databases.HealthDatabase
import com.example.qiuhao_zheng_myruns5.dialogs.CaloriesDialog
import com.example.qiuhao_zheng_myruns5.dialogs.CommentDialog
import com.example.qiuhao_zheng_myruns5.dialogs.DateDialog
import com.example.qiuhao_zheng_myruns5.dialogs.DistanceDialog
import com.example.qiuhao_zheng_myruns5.dialogs.DurationDialog
import com.example.qiuhao_zheng_myruns5.dialogs.HeartRateDialog
import com.example.qiuhao_zheng_myruns5.dialogs.TimeDialog
import java.util.Calendar

//import java.util.Calendar


class MenuEntryFragment : AppCompatActivity(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener{

    private lateinit var database: HealthDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var repository: DatabaseRepository
    private lateinit var viewModelFactory: DataViewModelFactory
    private lateinit var dataViewModel: HealthDataViewModel

    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var durationTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var heartRateTextView: TextView
    private lateinit var commentTextView: TextView
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button
    private lateinit var activityType: String
    private var dateValue: String = ""
    private var dateOnly: String = ""
    private var timeOnly: String = ""
    private var durationValue: String = "0mins 0secs"
    private var distanceValue: String = "0"
    private var caloriesValue: String = "0 cals"
    private var heartRateValue: String = "0 bmp"
    private var commentValue: String = ""
    private lateinit var unitSet: String

//    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu_entry_fragment)

        // set up tool bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val setUnitPrefer = sharedPreferences.getString("unitPrefer", "")
        unitSet = when (setUnitPrefer) {
            "Meter" -> {
                "Meter"
            }

            else -> {
                "Mile"
            }
        }

        database = HealthDatabase.getInstance(this)
        databaseDao = database.databaseDao
        repository = DatabaseRepository(databaseDao)
        viewModelFactory = DataViewModelFactory(repository)
        dataViewModel = ViewModelProvider(this, viewModelFactory).get(HealthDataViewModel::class.java)

        date = findViewById<TextView>(R.id.date)
        time = findViewById<TextView>(R.id.time)
        durationTextView = findViewById<TextView>(R.id.duration)
        distanceTextView = findViewById<TextView>(R.id.distance)
        caloriesTextView = findViewById<TextView>(R.id.calories)
        heartRateTextView = findViewById<TextView>(R.id.heartRate)
        commentTextView = findViewById<TextView>(R.id.comment)
        buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonCancel = findViewById<Button>(R.id.buttonCancel)
        // get the activity type
        val intent = intent
        intent?.let {
            activityType = it.getStringExtra("type").toString()
        }
        // set date
        date.setOnClickListener {
            val myDialog = DateDialog()
            val bundle = Bundle()
            bundle.putInt(DateDialog.DIALOG_KEY, DateDialog.TEST_DIALOG)
            myDialog.arguments = bundle
            myDialog.show(supportFragmentManager, "my dialog")
        }

        // set time
        time.setOnClickListener {
            val myDialog = TimeDialog()
            val bundle = Bundle()
            bundle.putInt(TimeDialog.DIALOG_KEY, TimeDialog.TEST_DIALOG)
            myDialog.arguments = bundle
            myDialog.show(supportFragmentManager, "my dialog")
        }

        durationTextView.setOnClickListener {
            editDuration()
//            println("qz: $durationValue")
        }
        distanceTextView.setOnClickListener {
            editDistance()
        }
        caloriesTextView.setOnClickListener {
            editCalories()
        }
        heartRateTextView.setOnClickListener {
            editHeartRate()
        }
        commentTextView.setOnClickListener {
            editComment()
        }
        // save button listener
        buttonSave.setOnClickListener {
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

//            setResult(Activity.RESULT_OK)
//            println("qz: $activityType")  // test use only
            // incert data into the database
            if (dateOnly == "") {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val monthOfYear = calendar.get(Calendar.MONTH) + 1
                val dateOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                when (monthOfYear) {
                    1 -> dateOnly = "Jan " + dateOfMonth.toString() + " " + year.toString()
                    2 -> dateOnly = "Feb " + dateOfMonth.toString() + " " + year.toString()
                    3 -> dateOnly = "Mar " + dateOfMonth.toString() + " " + year.toString()
                    4 -> dateOnly = "Apr " + dateOfMonth.toString() + " " + year.toString()
                    5 -> dateOnly = "May " + dateOfMonth.toString() + " " + year.toString()
                    6 -> dateOnly = "Jun " + dateOfMonth.toString() + " " + year.toString()
                    7 -> dateOnly = "Jul " + dateOfMonth.toString() + " " + year.toString()
                    8 -> dateOnly = "Aug " + dateOfMonth.toString() + " " + year.toString()
                    9 -> dateOnly = "Sept " + dateOfMonth.toString() + " " + year.toString()
                    10 -> dateOnly = "Oct " + dateOfMonth.toString() + " " + year.toString()
                    11 -> dateOnly = "Nov " + dateOfMonth.toString() + " " + year.toString()
                    12 -> dateOnly = "Dec " + dateOfMonth.toString() + " " + year.toString()
                }
            }
            if (timeOnly == "") {
                val calendar = Calendar.getInstance()

                val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val second = calendar.get(Calendar.SECOND)
                timeOnly = hourOfDay.toString() + ":" + minute.toString() + ":" + second.toString()
            }

            dateValue = timeOnly + " " + dateOnly
            val healthData = HealthData(
                inputType = "Manual Entry",
                type = activityType,
                date = dateValue,
                duration = durationValue,
                distance = distanceValue,
                calories = caloriesValue,
                heartRate = heartRateValue,
                comment = commentValue,
//                startPTLat = "",
//                startPTLng = "",
//                endPTLat = "",
//                endPTLng = "",
            )
            dataViewModel.insert(healthData)
            dateValue = ""
            dateOnly = ""
            timeOnly = ""
            durationValue = "0mins 0secs"
            distanceValue = "0"
            caloriesValue = "0 cals"
            heartRateValue = "0 bpm"
            commentValue = ""
            finish()
        }
        // cancel button listener
        buttonCancel.setOnClickListener {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
//            setResult(Activity.RESULT_OK)
            dateValue = ""
            dateOnly = ""
            timeOnly = ""
            durationValue = "0mins 0secs"
            distanceValue = "0"
            caloriesValue = "0 cals"
            heartRateValue = "0 bpm"
            commentValue = ""
            finish()
        }

    }

    // use date input to set dateOnly value
    fun getDate(year: Int, monthOfYear: Int, dateOfMonth: Int) {
//        println("qzz: $year, $monthOfYear, $dateOfMonth") // test use only
        when (monthOfYear) {
            1 -> dateOnly = "Jan " + dateOfMonth.toString() + " " + year.toString()
            2 -> dateOnly = "Feb " + dateOfMonth.toString() + " " + year.toString()
            3 -> dateOnly = "Mar " + dateOfMonth.toString() + " " + year.toString()
            4 -> dateOnly = "Apr " + dateOfMonth.toString() + " " + year.toString()
            5 -> dateOnly = "May " + dateOfMonth.toString() + " " + year.toString()
            6 -> dateOnly = "Jun " + dateOfMonth.toString() + " " + year.toString()
            7 -> dateOnly = "Jul " + dateOfMonth.toString() + " " + year.toString()
            8 -> dateOnly = "Aug " + dateOfMonth.toString() + " " + year.toString()
            9 -> dateOnly = "Sept " + dateOfMonth.toString() + " " + year.toString()
            10 -> dateOnly = "Oct " + dateOfMonth.toString() + " " + year.toString()
            11 -> dateOnly = "Nov " + dateOfMonth.toString() + " " + year.toString()
            12 -> dateOnly = "Dec " + dateOfMonth.toString() + " " + year.toString()
        }
    }

    fun getTime(hourOfDay: Int, minute: Int) {
        timeOnly = hourOfDay.toString() + ":" + minute.toString() + ":" + "00"
    }

    // edit duration
    private fun editDuration() {
        val myDialog = DurationDialog()
        val bundle = Bundle()
        bundle.putInt(DurationDialog.DIALOG_KEY, DurationDialog.TEST_DIALOG)
        myDialog.arguments = bundle
        myDialog.show(supportFragmentManager, "edit duration dialog")
    }

    fun getDuration(duration: String) {
//        println("qzz: $duration")
        if (duration.isNotEmpty()) {
            val durationNum = duration.toDouble()
            val intPart = durationNum.toInt()
            val decimalPart = durationNum - intPart

            durationValue = intPart.toString() + "mins " + (decimalPart * 60).toInt().toString() + "secs"
        }
    }

    // edit distance
    private fun editDistance() {
        val myDialog = DistanceDialog()
        val bundle = Bundle()
        bundle.putInt(DistanceDialog.DIALOG_KEY, DistanceDialog.TEST_DIALOG)
        myDialog.arguments = bundle
        myDialog.show(supportFragmentManager, "edit distance dialog")
    }

    fun getDistance(distance: String) {
//        println("qzz: $distance")
//        if (distance.isNotEmpty()) {
//            distanceValue = distance
//        }
        if (distance.isNotEmpty()) {
            if (unitSet == "Meter") {
                distanceValue = (distance.toDouble() * 0.621371).toString()
            } else {
                distanceValue = distance
            }
        }
    }

    // edit calories
    private fun editCalories() {
        val myDialog = CaloriesDialog()
        val bundle = Bundle()
        bundle.putInt(CaloriesDialog.DIALOG_KEY, CaloriesDialog.TEST_DIALOG)
        myDialog.arguments = bundle
        myDialog.show(supportFragmentManager, "edit calories dialog")
    }

    fun getCalories(calories: String) {
//        println("qzz: $calories")
        if (calories.isNotEmpty()) {
            caloriesValue = calories + " cals"
        }
    }


    // edit heart rate
    private fun editHeartRate() {
        val myDialog = HeartRateDialog()
        val bundle = Bundle()
        bundle.putInt(HeartRateDialog.DIALOG_KEY, HeartRateDialog.TEST_DIALOG)
        myDialog.arguments = bundle
        myDialog.show(supportFragmentManager, "edit heart rate dialog")
    }

    fun getHeartRate(heartRate: String) {
//        println("qzz: heartRate")
        if (heartRate.isNotEmpty()) {
            heartRateValue = heartRate + " bmp"
        }
    }

    // edit comment
    private fun editComment() {
        val myDialog = CommentDialog()
        val bundle = Bundle()
        bundle.putInt(CommentDialog.DIALOG_KEY, CommentDialog.TEST_DIALOG)
        myDialog.arguments = bundle
        myDialog.show(supportFragmentManager, "edit comment dialog")
    }

    fun getComment(comment: String) {
//        println("qzz: comment")
        if (comment.isNotEmpty()) {
            commentValue = comment
        }
    }

    // date
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
    }
    // time
    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    }
}