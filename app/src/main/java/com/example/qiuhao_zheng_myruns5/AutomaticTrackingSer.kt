package com.example.qiuhao_zheng_myruns5

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.File
import java.util.concurrent.ArrayBlockingQueue


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isCancelled
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*


class AutomaticTrackingSer : Service(), SensorEventListener {
    private val PENDINGINTENT_REQUEST_CODE = 0
    private val NOTIFY_ID = 112
    private val CHANNEL_ID = "notification channel"
    private lateinit var myBroadcastReceiver: MyBroadcastReceiver
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationManager: LocationManager
    private lateinit var myBinder: MyBinder
    private var msgHandler: Handler? = null

//    var typeDetection: Job? = null
    private lateinit var mAsyncTask: OnSensorChangedTask

    // varables for sensor
    private val mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 2
    private lateinit var mFeatureFile: File
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private var mServiceTaskType = 0
    private lateinit var mLabel: String
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>

    companion object{
        val STOP_SERVICE_ACTION = "stop service action"
        const val LOCATION_KEY = "location key"
        const val MSG_LOCATION_VALUE = 0
    }

    override fun onCreate() {
        super.onCreate()
//        println("debug: onCreate called")
        createChannel()
        val builder = NotificationCompat.Builder(this, "notification channel")
            .setContentTitle("Myruns")
            .setContentText("Recording your path now")
            .setSmallIcon(R.drawable.rocket)
            .setSilent(true)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFY_ID, builder.build())
        showNotification()

        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)

//        typeDetection = CoroutineScope(IO).launch{
//            typeDetection()
//        }

        myBroadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(STOP_SERVICE_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(myBroadcastReceiver, intentFilter)
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        myBinder = MyBinder()
        setupBroadcastReceiver()
        initLocationTracking()


    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "notification channel",
                "Map Tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        println("debug: onStartCommand called")
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            ?: throw IllegalStateException("No accelerometer sensor found")
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)

        mAsyncTask = OnSensorChangedTask()
        mAsyncTask.execute()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
//        unregisterReceiver(myBroadcastReceiver)
        notificationManager.cancel(NOTIFY_ID)
//        println("debug: onDestroy called")
//        typeDetection?.cancel()
    }

    private fun setupBroadcastReceiver() {
        myBroadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter(STOP_SERVICE_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(myBroadcastReceiver, intentFilter)
        }
    }

    private fun initLocationTracking() {
        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            location?.let {
                sendLocationUpdate(it)
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000, // 1 second
                0.2f,   // meter
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            sendLocationUpdate(location)
        }
    }
    private fun sendLocationUpdate(location: Location) {
        msgHandler?.let { handler ->
            val bundle = Bundle().apply {
                putParcelable(LOCATION_KEY, location)
            }
            val message = handler.obtainMessage().apply {
                data = bundle
                what = MSG_LOCATION_VALUE
            }
            handler.sendMessage(message)
        }
    }


    override fun onBind(intent: Intent): IBinder? {
//        return null
        return myBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        msgHandler = null
        return true
    }

    inner class MyBinder : Binder() {
        fun setMsgHandler(handler: Handler) {
            this@AutomaticTrackingSer.msgHandler = handler
        }
    }


    private fun showNotification() {
        val backIntent = Intent(this, Automatic::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            PENDINGINTENT_REQUEST_CODE,
            backIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Myruns")
            .setContentText("Recording your path now")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.rocket)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)

        val notification = notificationBuilder.build()

        startForeground(NOTIFY_ID, notification)

    }


    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopSelf()
            notificationManager.cancel(NOTIFY_ID)
            unregisterReceiver(myBroadcastReceiver)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val m = Math.sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1] + (event.values[2]
                    * event.values[2])).toDouble())

            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.
            try {
                mAccBuffer.add(m)
            } catch (e: IllegalStateException) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(m)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun sendMessage(message: String) {
        val intent = Intent("TYPE")
        intent.putExtra("message", message)
        sendBroadcast(intent)
    }


//    private suspend fun typeDetection() {
    inner class OnSensorChangedTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg arg0: Void?): Void? {
            var blockSize = 0
            val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            var max = Double.MIN_VALUE
            val inst = ArrayList<Double>(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled() == true) {
                        return null
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().toDouble()
                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0

                        // time = System.currentTimeMillis();
                        max = .0
                        for (`val` in accBlock) {
                            if (max < `val`) {
                                max = `val`
                            }
                        }
                        fft.fft(accBlock, im)
                        for (i in accBlock.indices) {
                            val mag = Math.sqrt(accBlock[i] * accBlock[i] + im[i]
                                    * im[i])
    //                        inst.add(i, mag)
                            im[i] = .0 // Clear the field
                            inst.add(mag)
                        }
                        inst.add(max)
                        val typeValue = WekaClassifier.classify(inst.toArray()).toInt()

                        println("qz: $typeValue")
                        if (typeValue == 0) {
                            sendMessage("Standing")
                        } else if (typeValue == 1) {
                            sendMessage("Walking")
                        } else {
                            sendMessage("Running")
                        }

                        inst.clear()

    //                    // Append max after frequency component
    //                    inst.setValue(Globals.ACCELEROMETER_BLOCK_CAPACITY, max)
    //                    inst.setValue(mClassAttribute, mLabel)
    //                    mDataset.add(inst)
    //                    Log.i("new instance", mDataset.size.toString() + "")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }




    }
}