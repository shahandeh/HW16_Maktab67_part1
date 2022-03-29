package com.example.hw16_maktab67_part1.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task WHERE username = :username")
    fun getTaskList(username: String): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE username = :username AND taskStatus = :taskStatus")
    suspend fun getTaskByFilter(username: String, taskStatus: TaskStatus): List<Task>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Delete
    suspend fun removeTask(task: Task)

    @Query("DELETE FROM task WHERE username = :username")
    suspend fun removeAll(username: String)

    @Update
    suspend fun updateTask(task: Task)

}