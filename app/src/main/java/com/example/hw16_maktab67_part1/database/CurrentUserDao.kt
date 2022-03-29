package com.example.hw16_maktab67_part1.database

import androidx.room.*

@Dao
interface CurrentUserDao {

    @Query("SELECT * FROM currentUser")
    suspend fun getUser() : CurrentUser

    @Insert
    suspend fun setCurrentUser(currentUser: CurrentUser)

    @Query("DELETE FROM currentUser")
    suspend fun deleteCurrentUserList()
}