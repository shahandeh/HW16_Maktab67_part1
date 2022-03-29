package com.example.hw16_maktab67_part1.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hw16_maktab67_part1.database.AppDataSource
import com.example.hw16_maktab67_part1.ui.doing.DoingFragmentViewModel
import com.example.hw16_maktab67_part1.ui.done.DoneFragmentViewModel
import com.example.hw16_maktab67_part1.ui.main.MainFragmentViewModel
import com.example.hw16_maktab67_part1.ui.todo.TodoFragmentViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val appDataSource: AppDataSource) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        when {
            modelClass.isAssignableFrom(MainFragmentViewModel::class.java) -> return MainFragmentViewModel(appDataSource) as T
            modelClass.isAssignableFrom(TodoFragmentViewModel::class.java) -> return TodoFragmentViewModel(appDataSource) as T
            modelClass.isAssignableFrom(DoingFragmentViewModel::class.java) -> return DoingFragmentViewModel(appDataSource) as T
            modelClass.isAssignableFrom(DoneFragmentViewModel::class.java) -> return DoneFragmentViewModel(appDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}