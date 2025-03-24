package com.example.qiuhao_zheng_myruns5

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)


        // set default value for unitPrefer
        if (!sharedPreferences.contains("unitPrefer")) {
            sharedPreferences.edit()
                .putString("unitPrefer", "Mile")
                .apply()
        }
//        if (Build.VERSION.SDK_INT >= 26) {
//            val channel = NotificationChannel(
//                "notification channel",
//                "Map Tracking",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//        val builder = NotificationCompat.Builder(this, "notification channel")
//            .setContentTitle("Prepare...")
//            .setContentText("Test...")
//            .setSmallIcon(R.drawable.rocket)
//            .setAutoCancel(true)
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(110, builder.build())
//        notificationManager.cancel(110)

    }

}