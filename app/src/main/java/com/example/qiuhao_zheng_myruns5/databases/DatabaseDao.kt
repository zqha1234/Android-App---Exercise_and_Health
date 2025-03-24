package com.example.qiuhao_zheng_myruns5.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


//XD: At compile time, Room automatically generates implementations of the custom DAO interface that you define.
// Click the middle mouse button (Windows users) to see the code generated automatically by the system.
@Dao
interface DatabaseDao {

    @Insert
    suspend fun insertData(healthData: HealthData)

    //A Flow is an async sequence of values
    //Flow produces values one at a time (instead of all at once) that can generate values
    //from async operations like network requests, database calls, or other async code.
    //It supports coroutines throughout its API, so you can transform a flow using coroutines as well!
    //Code inside the flow { ... } builder block can suspend. So the function is no longer marked with suspend modifier.
    //See more details here: https://kotlinlang.org/docs/flow.html#flows
    @Query("SELECT * FROM healthData_table")
    fun getAllData(): Flow<List<HealthData>>

    @Query("DELETE FROM healthData_table")
    suspend fun deleteAll()

    @Query("DELETE FROM healthData_table WHERE id = :key") //":" indicates that it is a Bind variable
    suspend fun deleteData(key: Long)
}