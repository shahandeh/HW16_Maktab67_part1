package com.example.hw16_maktab67_part1.ui.model

import com.example.hw16_maktab67_part1.database.TaskStatus

interface CreateTaskInterface {

    fun datePicker()

    fun timePicker()

    fun pictureFromGallery()

    fun pictureFromCamera()

    fun save(title: String, description: String, taskStatus: TaskStatus)

}