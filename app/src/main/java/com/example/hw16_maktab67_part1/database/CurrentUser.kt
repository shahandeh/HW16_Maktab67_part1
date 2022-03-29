package com.example.hw16_maktab67_part1.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentUser")
data class CurrentUser(
    @PrimaryKey
    val username: String
)