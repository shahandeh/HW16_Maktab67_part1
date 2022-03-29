package com.example.hw16_maktab67_part1.ui.model

import com.example.hw16_maktab67_part1.database.Task

interface RecyclerViewListener {

    fun recyclerViewClick(task: Task, position: Int)

    fun newTaskAdded()

    fun dataSetChanged()

}