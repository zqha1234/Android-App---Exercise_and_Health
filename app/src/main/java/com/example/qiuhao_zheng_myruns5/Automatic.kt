package com.example.qiuhao_zheng_myruns5

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.qiuhao_zheng_myruns5.databases.DataViewModelFactory
import com.example.qiuhao_zheng_myruns5.databases.DatabaseDao
import com.example.qiuhao_zheng_myruns5.databases.DatabaseRepository
import com.example.qiuhao_zheng_myruns5.databases.HealthData
import com.example.qiuhao_zheng_myruns5.databases.HealthDataViewModel
import com.example.qiuhao_zheng_myruns5.databases.HealthDatabase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.Calendar
import kotlin.math.abs


class Automatic : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    private lateinit var locationManager: LocationManager
    private var mapCenteredFirstTime = false
    private var mapDragged = false
    private lateinit var mMap: GoogleMap

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var setUnitPrefer: String
    private lateinit var activityType: String

    private lateinit var database: HealthDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var repository: DatabaseRepository
    private lateinit var viewModelFactory: DataViewModelFactory
    private lateinit var dataViewModel: HealthDataViewModel

    private lateinit var stLat: String
    private lateinit var stLng: String
    private lateinit var endLat: String
    private lateinit var endLng: String

    private var startTime: Long = 0
    private var lastLocation: Location? = null
    private var lastTime: Long = 0
    private var distanceValue: Float = 0f
    private var climbValue: Float = 0f
    private var avgSpeed: Float = 0f
    private var currSpeed: Float = 0f
    private var calories: Int = 0
    private val locationList = mutableListOf<Location>()

    private lateinit var typeTextView: TextView
    private lateinit var avgSpeedTextView: TextView
    private lateinit var curSpeedTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var climbTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var recMsg: String

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recMsg = intent?.getStringExtra("message").toString()
            activityType = recMsg
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gps)

        buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonCancel = findViewById<Button>(R.id.buttonCancel)

        // set up tool bar
        val toolbar: Toolbar = findViewById(R.id.gpstoolbar)
        setSupportActionBar(toolbar)

        // start time
        startTime = System.currentTimeMillis()
        lastTime = startTime

        // initiate map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
//        initLocationManager()

        registerReceiver(receiver, IntentFilter("TYPE"))

        database = HealthDatabase.getInstance(this)
        databaseDao = database.databaseDao
        repository = DatabaseRepository(databaseDao)
        viewModelFactory = DataViewModelFactory(repository)
        dataViewModel = ViewModelProvider(this, viewModelFactory).get(HealthDataViewModel::class.java)

        // get the activity type
//        val intent = intent
//        intent?.let {
//            activityType = it.getStringExtra("type").toString()
//        }

        activityType = "Standing"

        // get unit prefer value
        sharedPreferences = this.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        setUnitPrefer = sharedPreferences.getString("unitPrefer", "").toString()

        typeTextView = findViewById<TextView>(R.id.typeStats)
        avgSpeedTextView = findViewById<TextView>(R.id.avgSpeed)
        curSpeedTextView = findViewById<TextView>(R.id.curSpeed)
        caloriesTextView = findViewById<TextView>(R.id.calorieConsum)
        climbTextView = findViewById<TextView>(R.id.climb)
        distanceTextView = findViewById<TextView>(R.id.distanceFromOrigin)


        currSpeed = 0.toFloat()



        typeTextView.text = "Type: " + activityType
        if (setUnitPrefer == "Mile") {
            avgSpeedTextView.text = String.format("Avg speed: %.2f m/h", avgSpeed * 2.237)
            curSpeedTextView.text = String.format("Cur speed: %.2f m/h", currSpeed * 2.237f)
            distanceTextView.text = String.format("Distance: %.2f m", distanceValue / 1609.34)
            climbTextView.text = String.format("Climb: %.2f m", climbValue / 1609.34)
            caloriesTextView.text = String.format("Calories: %d", calories)
        } else {
//                val avgSpeedView = avgSpeed * 3.6f // convert to km/
            avgSpeedTextView.text = String.format("Avg speed: %.2f km/h", avgSpeed * 3.6f)
            curSpeedTextView.text = String.format("Cur speed: %.2f km/h", currSpeed * 3.6f)
            distanceTextView.text = String.format("Distance: %.2f km", distanceValue / 1000)
            climbTextView.text = String.format("Climb: %.2f km", climbValue / 1000)
            caloriesTextView.text = String.format("Calories: %d", calories)
        }


        // save button listener
        buttonSave.setOnClickListener {
            val intent = Intent()
            intent.action = TrackingService.STOP_SERVICE_ACTION
            sendBroadcast(intent)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
//            setResult(Activity.RESULT_OK)
            var dateOnly: String = ""
            var timeOnly: String = ""
            var dateValue: String = ""
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val monthOfYear = calendar.get(Calendar.MONTH) + 1
            val dateOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)
            timeOnly = hourOfDay.toString() + ":" + minute.toString() + ":" + second.toString()
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
            dateValue = timeOnly + " " + dateOnly
            var pathJson: String
//            if (locationList.isNotEmpty()) {
            stLat = locationList.first().latitude.toString()
            stLng = locationList.first().longitude.toString()
            endLat = locationList.last().latitude.toString()
            endLng = locationList.last().longitude.toString()
            pathJson = pTsToString(locationList)

            val durationNum = System.currentTimeMillis() - startTime
            val intPart = (durationNum / 1000 / 60).toInt()
            val decimalPart = (durationNum / 1000 % 60).toInt()

            val durationValue = intPart.toString() + "mins " + (decimalPart).toInt().toString() + "secs"


            val healthData = HealthData(
                inputType = "Automatic",
                type = activityType,
                date = dateValue,
                calories = calories.toString(),
                distance = (distanceValue / 1609.34).toString(),
                duration = durationValue,
                avgSpeedDB = (avgSpeed* 2.237).toString(),
                climb = (climbValue / 1609.34).toString(),
//                heartRate = heartRateValue,
                startPTLat = stLat,
                startPTLng = stLng,
                endPTLat = endLat,
                endPTLng = endLng,
                path = pathJson
            )
            dataViewModel.insert(healthData)
            startTime = 0
            lastLocation = null
            lastTime = 0
            distanceValue = 0f
            climbValue = 0f
            avgSpeed = 0f
            currSpeed = 0f
            calories = 0
            stLat = ""
            stLng = ""
            endLat = ""
            endLng = ""

            finish()
        }
        // cancel button listener
        buttonCancel.setOnClickListener {
            val intent = Intent()
            intent.action = TrackingService.STOP_SERVICE_ACTION
            sendBroadcast(intent)
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
//            setResult(Activity.RESULT_OK)
//            println("qz: $activityType")  // test use only
            startTime = 0
            lastLocation = null
            lastTime = 0
            distanceValue = 0f
            climbValue = 0f
            avgSpeed = 0f
            currSpeed = 0f
            calories = 0
            stLat = ""
            stLng = ""
            endLat = ""
            endLng = ""

            finish()
        }
    }

    private fun pTsToString(locations: List<Location>): String {
        return locations.joinToString(";") { location ->
            "${location.latitude},${location.longitude}"
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
//        val defaultLocation = LatLng(0.0, 0.0)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2f))


        if (checkPermission()) {
            initLocationManager()
//            println("qz: 3")
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = false

        }
        mMap.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                mapDragged = true

                Handler(Looper.getMainLooper()).postDelayed({
//                    if (locChanged) {
                    mapDragged = false
//                    }
                }, 1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            locationManager.removeUpdates(this)
    }


    fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null)
                onLocationChanged(location)

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        } catch (e: SecurityException) {
        }
    }

    override fun onLocationChanged(location: Location) {
//        println("debug: onlocationchanged() ${location.latitude} ${location.longitude}")
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        val projection = mMap.projection
        val centerPT = projection.toScreenLocation(latLng)
        val mView = findViewById<View>(R.id.map)
        val ptX = mView.width / 2
        val ptY = mView.height / 2
        val isPTCentered = (abs(centerPT.x - ptX) <= 50 &&
                abs(centerPT.y - ptY) <= 50)
        // make the map center
        if (!mapDragged and !isPTCentered and mapCenteredFirstTime) {
//            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            val cameraUpdate = CameraUpdateFactory.newLatLng(latLng)
            mMap.animateCamera(cameraUpdate)

        }
        if (!mapCenteredFirstTime) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//            val cameraUpdate = CameraUpdateFactory.zoomTo(16f)
            Handler(Looper.getMainLooper()).postDelayed({
                val cameraUpdate = CameraUpdateFactory.zoomTo(16f)
                mMap.animateCamera(cameraUpdate, 1150, object : GoogleMap.CancelableCallback {
                    override fun onCancel() {}

                    override fun onFinish() {
                        mapCenteredFirstTime = true
                    }
                })
            }, 1150)
//            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
//            mMap.animateCamera(cameraUpdate)
//            mapCenteredFirstTime = true
        }


        val currTime = System.currentTimeMillis()
        if (currTime - lastTime > 1000) { // update data every 1s
            locationList.add(location)
            updateData(location)
//            drawLine()
            lastTime = currTime
        }
        drawLine()
    }

    private fun updateData(newLocation: Location) {

        lastLocation?.let { last ->
            // update distance
            val distance = last.distanceTo(newLocation)
            distanceValue += distance
            if (distance > 0.05) {
                currSpeed = newLocation.speed
            } else {
                currSpeed = 0.toFloat()
            }
//            currSpeed = distance / 0.5.toFloat()
            // update climb
            val diffAlti = newLocation.altitude - last.altitude
            if(diffAlti > 0) {
                climbValue += diffAlti.toFloat()
            }

            // updata calories - calculation found online
            calories = (distanceValue / 1000 * 65 * 1.036).toInt()
        }

        lastLocation = newLocation

        // update data on screen (update ui)
        updateUIView()
    }

    // update ui
    private fun updateUIView() {
        // calculate speed (m/s)
        val timeTotal = (System.currentTimeMillis() - startTime) / 1000f //meters/s
        avgSpeed = if(timeTotal > 0) {
            (distanceValue / timeTotal)
        } else 0f
        // main thread update ui

        runOnUiThread {
            typeTextView.text = "Type: " + activityType
            if (setUnitPrefer == "Mile") {
                avgSpeedTextView.text = String.format("Avg speed: %.2f m/h", avgSpeed * 2.237)
                curSpeedTextView.text = String.format("Cur speed: %.2f m/h", currSpeed * 2.237f)
                distanceTextView.text = String.format("Distance: %.2f m", distanceValue / 1609.34)
                climbTextView.text = String.format("Climb: %.2f m", climbValue / 1609.34)
                caloriesTextView.text = String.format("Calories: %d", calories)
            } else {
//                val avgSpeedView = avgSpeed * 3.6f // convert to km/
                avgSpeedTextView.text = String.format("Avg speed: %.2f km/h", avgSpeed * 3.6f)
                curSpeedTextView.text = String.format("Cur speed: %.2f km/h", currSpeed * 3.6f)
                distanceTextView.text = String.format("Distance: %.2f km", distanceValue / 1000)
                climbTextView.text = String.format("Climb: %.2f km", climbValue / 1000)
                caloriesTextView.text = String.format("Calories: %d", calories)
            }
        }
    }

    // modify code - Nov 6, 2024
    private fun drawLine() {
        if(locationList.size < 2) return

        val pt = locationList.map {
            LatLng(it.latitude, it.longitude)
        }

        mMap.clear()

        val polylineOptions = PolylineOptions()
            .addAll(pt)
            .width(5f)
            .color(Color.BLACK)
        mMap.addPolyline(polylineOptions)

        val startPoint = pt.first()
        mMap.addMarker(MarkerOptions().position(startPoint).title("S").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        val endPoint = pt.last()
        mMap.addMarker(MarkerOptions().position(endPoint).title("E"))

    }

    private fun checkPermission(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return (permissionStatus == PackageManager.PERMISSION_GRANTED)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // stop service
        val intent = Intent()
        intent.action = TrackingService.STOP_SERVICE_ACTION
        sendBroadcast(intent)

    }

}