package com.example.hw16_maktab67_part1.database

import androidx.room.*

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profile WHERE username = :username")
    suspend fun getUser(username: String) : Profile

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registerUser(profile: Profile)

}