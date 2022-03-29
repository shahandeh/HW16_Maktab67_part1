package com.example.hw16_maktab67_part1.ui.model

import com.example.hw16_maktab67_part1.database.TaskStatus

interface EditTaskInterface {

    fun deleteTask()

    fun datePicker()

    fun timePicker()

    fun pictureFromGallery()

    fun pictureFromCamera()

    fun save(description: String, taskStatus: TaskStatus)

    fun share()

}