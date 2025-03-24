package com.example.qiuhao_zheng_myruns5

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.qiuhao_zheng_myruns5.history.History
import com.example.qiuhao_zheng_myruns5.history.MyFragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var fragmentStart: Start
    private lateinit var fragmentHistory: History
    private lateinit var fragmentSetting: Setting
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var myMyFragmentStateAdapter: MyFragmentStateAdapter
    private lateinit var fragments: ArrayList<Fragment>
    private val tabTitles = arrayOf("Start", "History", "Setting") //Tab titles
    private lateinit var tabConfigurationStrategy: TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // set up tool bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
//        preNotification()
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                preNotification()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
        // check permissions
        Util.checkPermissions(this)

//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                createChannel()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        preNotification()
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                preNotification()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        viewPager2 = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tab)

        fragmentStart = Start()
        fragmentHistory = History()
        fragmentSetting = Setting()

        // add fragments to the list
        fragments = ArrayList()
        fragments.add(fragmentStart)
        fragments.add(fragmentHistory)
        fragments.add(fragmentSetting)

        myMyFragmentStateAdapter = MyFragmentStateAdapter(this, fragments)
        viewPager2.adapter = myMyFragmentStateAdapter

        tabConfigurationStrategy = TabConfigurationStrategy {
                tab: TabLayout.Tab, position: Int ->
            tab.text = tabTitles[position] }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    // override the function - onRequesetionPermissionsResult
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // call Util.requestPermissionsResult
        Util.requestPermissionsResult(this, requestCode, permissions, grantResults)
    }

//    private fun createChannel() {
//        if (Build.VERSION.SDK_INT >= 26) {
//            val channel = NotificationChannel(
//                "notification channel",
//                "Map Tracking",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

//    private fun preNotification() {
//        val builder = NotificationCompat.Builder(this, "notification channel")
//            .setContentTitle("Prepare...")
//            .setContentText("Test...")
//            .setSmallIcon(R.drawable.rocket)
//            .setAutoCancel(true)
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(110, builder.build())
//        notificationManager.cancel(110)
//    }

    // onDestory
    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent()
        intent.action = TrackingService.STOP_SERVICE_ACTION
        sendBroadcast(intent)
        tabLayoutMediator.detach()
    }

    // menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_optionmenu, menu)
        return true
    }

}