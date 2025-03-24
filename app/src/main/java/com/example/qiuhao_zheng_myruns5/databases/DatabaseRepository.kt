package com.example.qiuhao_zheng_myruns5.databases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

//A Repository manages queries and allows you to use multiple backends.
// In the most common example, the Repository implements the logic for
// deciding whether to fetch data from a network or use results cached in a local database.
class DatabaseRepository(private val databaseDao: DatabaseDao) {

    val allComments: Flow<List<HealthData>> = databaseDao.getAllData()
    // insert the data entry in database
    fun insert(healthData: HealthData){
        CoroutineScope(IO).launch{
            databaseDao.insertData(healthData)
        }
    }
    // delete the entry in database
    fun delete(id: Long){
        CoroutineScope(IO).launch {
            databaseDao.deleteData(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(IO).launch {
            databaseDao.deleteAll()
        }
    }
}