package com.example.kotlinsamples

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinsamples.data.User

class MainViewModel : ViewModel() {


    private val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also {
            loadUsers()
        }
    }

    fun getUsers() : LiveData<List<User>> {
        return users
    }

    private fun loadUsers() {

    }
}