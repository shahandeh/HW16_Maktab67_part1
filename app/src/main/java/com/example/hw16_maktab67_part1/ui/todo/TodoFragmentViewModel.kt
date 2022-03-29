package com.example.hw16_maktab67_part1.ui.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hw16_maktab67_part1.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*

class TodoFragmentViewModel(private val appDataSource: AppDataSource) : ViewModel() {


    lateinit var task: Task
    lateinit var date: String
    lateinit var time: String
    lateinit var taskStatus: TaskStatus
    lateinit var imageUri: String
    lateinit var description: String
    var position = -1

    private val currentUserDao : CurrentUserDao by lazy { appDataSource.currentUserDao() }
    private val taskDao : TaskDao by lazy { appDataSource.taskDao() }


    private val _currentUser: MutableLiveData<CurrentUser> = MutableLiveData()
    val currentUser: LiveData<CurrentUser> = _currentUser


    private val _todoTaskList : MutableLiveData<List<Task>> = MutableLiveData()
    var todoTaskList : MutableLiveData<List<Task>> = _todoTaskList


////////////////////////////////////////////////////////////////////////////////////////////////////


    fun getCurrentUser(){
        CoroutineScope(Dispatchers.IO).launch {
            _currentUser.postValue(currentUserDao.getUser())
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    fun getTodoTaskList() {
            CoroutineScope(Dispatchers.IO).launch {
                _todoTaskList.postValue(taskDao.getTaskByFilter(currentUser.value?.username.toString(), TaskStatus.TODO))
            }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    fun datePicker(context: Context){
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val datePickerDialog =
            DatePickerDialog(
                context,
                { _, Year, Month, Day -> date = "$Year/$Month/$Day" },
                year, month, day
            )
        datePickerDialog.show()
    }

    fun timePicker(context: Context){
        val calendar: Calendar = Calendar.getInstance()
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                time = "$hour:$minute"
            }
        TimePickerDialog(
            context,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun uriFromCamera(context: Context, bitmap: Bitmap){
        val fileName = System.currentTimeMillis().toString()
        try {
            context.openFileOutput("$fileName.jpg", Context.MODE_PRIVATE)
                .use { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                    imageUri =
                        Uri.fromFile(File("${context.filesDir}/$fileName.jpg"))
                            .toString()
                }
        } catch (e: IOException) {
            e.printStackTrace().toString()
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////


    fun removeTask(){
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.removeTask(task)
        }
    }

    fun updateTask(){

        val imageUriTemp = if (::imageUri.isInitialized) imageUri else null

        val descriptionTemp : String = if (::description.isInitialized) {
            description.ifBlank { task.description }
        } else {
            task.description
        }

        val dateTemp : String = if (::date.isInitialized) {
            date.ifBlank { task.date }
        } else {
            task.date
        }

        val timeTemp : String = if (::time.isInitialized) {
            time.ifBlank { task.time }
        } else {
            task.time
        }

        val newTask = task.copy(
            description = descriptionTemp,
            date = dateTemp,
            taskStatus = taskStatus,
            time = timeTemp,
            uri = imageUriTemp
        )

        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateTask(newTask)
        }
    }
}