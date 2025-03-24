package com.example.qiuhao_zheng_myruns5.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HealthData::class], version = 1)
abstract class HealthDatabase : RoomDatabase() { // Room automatically generates implementations of your abstract CommentDatabase class.
    abstract val databaseDao: DatabaseDao

    companion object{
        //The Volatile keyword guarantees visibility of changes to the INSTANCE variable across threads
        @Volatile
        private var INSTANCE: HealthDatabase? = null

        fun getInstance(context: Context) : HealthDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        HealthDatabase::class.java, "healthData_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}