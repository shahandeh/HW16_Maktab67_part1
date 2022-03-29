package com.example.hw16_maktab67_part1.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    @PrimaryKey
    val title: String,
    val username: String,
    val description: String,
    val taskStatus: TaskStatus,
    val time: String,
    val date: String,
    val uri: String? = null
)