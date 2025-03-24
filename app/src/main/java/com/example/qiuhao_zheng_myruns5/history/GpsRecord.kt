package com.example.qiuhao_zheng_myruns5.history

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.qiuhao_zheng_myruns5.R
import com.example.qiuhao_zheng_myruns5.databases.DataViewModelFactory
import com.example.qiuhao_zheng_myruns5.databases.DatabaseRepository
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


class GpsRecord : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var database: HealthDatabase
    private lateinit var repository: DatabaseRepository
    private lateinit var viewModelFactory: DataViewModelFactory
    private lateinit var viewModel: HealthDataViewModel

    private lateinit var locationManager: LocationManager
    private lateinit var mMap: GoogleMap

    private lateinit var activityType: String
    private lateinit var endPTLat: String
    private lateinit var endPTLng: String
    private lateinit var path: String

//    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var setUnitPrefer: String

//    private var startTime: Long = 0
//    private var lastTime: Long = 0
    private lateinit var distanceValue: String
    private lateinit var climbValue: String
    private lateinit var avgSpeed: String
//    private var currSpeed: Float = 0f
    private lateinit var calories: String
    private val locationList = mutableListOf<Location>()


    private lateinit var avgSpeedTextView: TextView
    private lateinit var curSpeedTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var climbTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var typeTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gpsrecord)


        // set up tool bar
        val toolbar: Toolbar = findViewById(R.id.gpstoolbar)
        setSupportActionBar(toolbar)

        database = HealthDatabase.getInstance(this)
        repository = DatabaseRepository(database.databaseDao)
        viewModelFactory = DataViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HealthDataViewModel::class.java]

        // get unit prefer value
//        sharedPreferences = this.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
//        setUnitPrefer = sharedPreferences.getString("unitPrefer", "").toString()


        val intent = intent
        // get the activity type
        activityType = intent.getStringExtra("type").toString()
        // get related values
        val recordID = intent.getLongExtra("record_id", -1)
        val stPTLat = intent.getStringExtra("startPTLat")
        val stPTLng = intent.getStringExtra("startPTLng")
        setUnitPrefer = intent.getStringExtra("unitPrefer") ?: ""
        distanceValue = intent.getStringExtra("distance") ?: ""
        calories = intent.getStringExtra("calories") ?: ""
        avgSpeed = intent.getStringExtra("avgSpeed") ?: ""
        climbValue = intent.getStringExtra("climb") ?: ""

        endPTLat = intent.getStringExtra("endPTLat").toString()
        endPTLng = intent.getStringExtra("endPTLng").toString()
        path = intent.getStringExtra("path").toString()


        typeTextView = findViewById<TextView>(R.id.typeStats)
        avgSpeedTextView = findViewById<TextView>(R.id.avgSpeed)
//        curSpeedTextView = findViewById<TextView>(R.id.curSpeed)
        caloriesTextView = findViewById<TextView>(R.id.calorieConsum)
        climbTextView = findViewById<TextView>(R.id.climb)
        distanceTextView = findViewById<TextView>(R.id.distanceFromOrigin)

        // start time
//        startTime = System.currentTimeMillis()
//        lastTime = startTime

        // initiate map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        updateUIView()

        typeTextView.text = "Type: " + activityType
        if (setUnitPrefer == "Miles") {
            avgSpeedTextView.text = String.format("Avg speed: %.2f m/h", avgSpeed.toFloat())
//            curSpeedTextView.text = String.format("Cur speed: na")
            distanceTextView.text = String.format("Distance: %.2f m", distanceValue.toFloat())
            climbTextView.text = String.format("Climb: %.2f m", climbValue.toFloat())
            caloriesTextView.text = String.format("Calories: %d", calories.toInt())
        } else {
            avgSpeedTextView.text = String.format("Avg speed: %.2f km/h", avgSpeed.toFloat() * 1.609)
//            curSpeedTextView.text = String.format("Cur speed: na")
            distanceTextView.text = String.format("Distance: %.2f km", distanceValue.toFloat() * 1.609)
            climbTextView.text = String.format("Climb: %.2f km", climbValue.toFloat() * 1.609)
            caloriesTextView.text = String.format("Calories: %d", calories.toInt())
        }

        findViewById<Button>(R.id.buttonDelete).setOnClickListener {
            viewModel.deleteById(recordID)
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

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
//        stLat = startPoint.latitude.toString()
//        stLng = startPoint.longitude.toString()
        val endPoint = pt.last()
        mMap.addMarker(MarkerOptions().position(endPoint).title("E"))
//        endLat = endPoint.latitude.toString()
//        endLng = endPoint.longitude.toString()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        if (checkPermission()) {
            initLocationManager()
//            println("qz: 3")
            mMap.isMyLocationEnabled = false
            mMap.uiSettings.isZoomControlsEnabled = false
            val lat = endPTLat.toDouble()
            val lng = endPTLng.toDouble()
//        println("qz: $lat $lng")
            if (lat != null && lng != null) {
                val latLng = LatLng(lat, lng)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                mMap.animateCamera(cameraUpdate)
            }

            val pts = path?.let { stringToPts(it) }
            locationList.clear()
            if (pts != null) {
                locationList.addAll(pts)
                drawLine()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (locationManager != null)
//            locationManager.removeUpdates(this)
    }

    fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

//            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return
//
//            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            if (location != null)
//                onLocationChanged(location)

//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        } catch (e: SecurityException) {
        }
    }


    private fun stringToPts(pathString: String): List<Location> {
        return pathString.split(";").map { pointStr ->
            val (lat, lng) = pointStr.split(",").map { it.toDouble() }
            Location("").apply {
                latitude = lat
                longitude = lng
            }
        }
    }

    // update ui
//    private fun updateUIView() {
//        // main thread update ui
//        runOnUiThread {
//            typeTextView.text = "Type: " + activityType
//            if (setUnitPrefer == "Mile") {
//                avgSpeedTextView.text = String.format("Avg speed: %.2f m/h", avgSpeed )
//                curSpeedTextView.text = String.format("Cur speed: na")
//                distanceTextView.text = String.format("Distance: %.2f m", distanceValue)
//                climbTextView.text = String.format("Climb: %.2f m", climbValue)
//                caloriesTextView.text = String.format("Calories: %d", calories)
//            } else {
////                val avgSpeedView = avgSpeed * 3.6f // convert to km/
////                avgSpeedTextView.text = String.format("Avg speed: %.2f km/h", avgSpeed.toDouble() * 1.609)
////                curSpeedTextView.text = String.format("Cur speed: %.2f na")
////                distanceTextView.text = String.format("Distance: %.2f km", distanceValue.toDouble() * 1.609)
////                climbTextView.text = String.format("Climb: %.2f km", climbValue.toDouble() * 1.609)
////                caloriesTextView.text = String.format("Calories: %d", calories)
//            }
//        }
//    }


//
    private fun checkPermission(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return (permissionStatus == PackageManager.PERMISSION_GRANTED)
    }
}