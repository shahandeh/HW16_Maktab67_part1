package com.example.hw16_maktab67_part1.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hw16_maktab67_part1.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragmentViewModel(private val appDataSource: AppDataSource) : ViewModel() {

    private val currentUserDao: CurrentUserDao by lazy { appDataSource.currentUserDao() }
    private val profileDao: ProfileDao by lazy { appDataSource.profileDao() }
    private val taskDao: TaskDao by lazy { appDataSource.taskDao() }

    lateinit var name: String
    lateinit var username: String
    lateinit var email: String
    lateinit var password: String

    lateinit var createTaskUsername: String
    lateinit var createTaskTitle: String
    lateinit var createTaskDescription: String
    lateinit var createTaskStatus: TaskStatus
    lateinit var createTaskTime: String
    lateinit var createTaskDate: String
    var createTaskPhotoUri: String? = null

    private val _profileIsValid: MutableLiveData<Boolean> = MutableLiveData()
    val profileIsValid: LiveData<Boolean> get() = _profileIsValid

    private val _profile: MutableLiveData<Profile> = MutableLiveData()
    val profile: LiveData<Profile> = _profile

    private val _taskList : MutableLiveData<List<Task>> = MutableLiveData()
    val taskList : LiveData<List<Task>> get() = _taskList


////////////////////////////////////////////////////////////////////////////////////////////////////


    init {
        CoroutineScope(Dispatchers.IO).launch { currentUserDao.deleteCurrentUserList() }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

    fun register() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDao.registerUser(
                Profile(
                    username = username,
                    name = name,
                    email = email,
                    password = password
                )
            )
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getUser() {
        CoroutineScope(Dispatchers.IO).launch {
            _profile.postValue(profileDao.getUser(username))
        }
    }

    fun userValidate() {
        if (profile.value?.password == password) {
            _profileIsValid.value = true
        }
    }

    fun setCurrentUser() {
        CoroutineScope(Dispatchers.IO).launch {
            currentUserDao.setCurrentUser(
                CurrentUser(
                    username = profile.value?.username.toString()
                )
            )

        }
    }

    fun removeCurrentUser(){
        CoroutineScope(Dispatchers.IO).launch {
            currentUserDao.deleteCurrentUserList()
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    fun addTask() {
        val temp = Task(
            title = createTaskTitle,
            username = createTaskUsername,
            description = createTaskDescription,
            taskStatus = createTaskStatus,
            time = createTaskTime,
            date = createTaskDate,
            uri = createTaskPhotoUri
        )
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.addTask(temp)
        }
    }
    fun getTaskList(){
        CoroutineScope(Dispatchers.IO).launch { _taskList.postValue(taskDao.getTaskByFilter(createTaskUsername, createTaskStatus)) }
    }

    fun removeAllTask(username: String){
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.removeAll(username)
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
}