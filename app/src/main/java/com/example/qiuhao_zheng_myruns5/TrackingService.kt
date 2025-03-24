package com.example.qiuhao_zheng_myruns5

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TrackingService : Service() {
    private val PENDINGINTENT_REQUEST_CODE = 0
    private val NOTIFY_ID = 11
    private val CHANNEL_ID = "notification channel"
    private lateinit var myBroadcastReceiver: MyBroadcastReceiver
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationManager: LocationManager
    private lateinit var myBinder: MyBinder
    private var msgHandler: Handler? = null

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
//            .setAutoCancel(true)
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(110, builder.build())
//        notificationManager.cancel(110)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFY_ID, builder.build())
//        startForeground(NOTIFY_ID, builder.build())
//        createChannel()
        showNotification()

        myBroadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(STOP_SERVICE_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(myBroadcastReceiver, intentFilter)
        }

//        super.onCreate()
//        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
//        unregisterReceiver(myBroadcastReceiver)
        notificationManager.cancel(NOTIFY_ID)
//        println("debug: onDestroy called")
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
            this@TrackingService.msgHandler = handler
        }
    }


    private fun showNotification() {
//        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
//            this,
//            CHANNEL_ID
//        )
//        notificationBuilder.setContentTitle("Myruns")
//        notificationBuilder.setContentText("Recording your path now")
//        notificationBuilder.setSmallIcon(R.drawable.rocket)
//        var notification = notificationBuilder.build()
        val backIntent = Intent(this, GPS::class.java).apply {
//            action = Intent.ACTION_MAIN
//            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            PENDINGINTENT_REQUEST_CODE,
            backIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

//        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
//            this,
//            CHANNEL_ID
//        )
//        notificationBuilder.setContentTitle("Myruns")
//        notificationBuilder.setContentText("Recording your path now")
//        notificationBuilder.setContentIntent(pendingIntent)
//        notificationBuilder.setSmallIcon(R.drawable.rocket)
//        notificationBuilder.setAutoCancel(false)
//        notificationBuilder.setSilent(true)
//        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//        notificationBuilder.setOngoing(true)
////        notification = notificationBuilder.build()
//        val notification = notificationBuilder.build()

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

//        if (Build.VERSION.SDK_INT > 26) {
//            val notificationChannel = NotificationChannel(CHANNEL_ID,
//                "channel name", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(notificationChannel)
//        }

        startForeground(NOTIFY_ID, notification)

    }


    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopSelf()
            notificationManager.cancel(NOTIFY_ID)
            unregisterReceiver(myBroadcastReceiver)
        }
    }
}