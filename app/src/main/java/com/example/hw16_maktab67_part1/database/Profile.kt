package com.example.hw16_maktab67_part1.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile (
    @PrimaryKey
    val username: String,
    val name: String,
    val email: String,
    val password: String
        )