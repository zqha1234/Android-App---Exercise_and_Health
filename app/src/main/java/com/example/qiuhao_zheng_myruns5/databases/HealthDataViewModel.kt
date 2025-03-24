package com.example.qiuhao_zheng_myruns5.databases

import androidx.lifecycle.*
import java.lang.IllegalArgumentException


class HealthDataViewModel(private val repository: DatabaseRepository) : ViewModel() {
    val allCommentsLiveData: LiveData<List<HealthData>> = repository.allComments.asLiveData()

    fun insert(healthData: HealthData) {
        repository.insert(healthData)
    }

    fun deleteById(id: Long) {
        repository.delete(id)
    }

    fun deleteFirst(){
        val commentList = allCommentsLiveData.value
        if (commentList != null && commentList.size > 0){
            val id = commentList[0].id
            repository.delete(id)
        }
    }

    fun deleteAll(){
        val commentList = allCommentsLiveData.value
        if (commentList != null && commentList.size > 0)
            repository.deleteAll()
    }
}

class DataViewModelFactory (private val repository: DatabaseRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(HealthDataViewModel::class.java))
            return HealthDataViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}