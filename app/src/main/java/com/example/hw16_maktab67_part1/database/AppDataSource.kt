package com.example.hw16_maktab67_part1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrentUser::class, Profile::class, Task::class], version = 3, exportSchema = true)
abstract class AppDataSource: RoomDatabase() {

    abstract fun currentUserDao() : CurrentUserDao
    abstract fun profileDao() : ProfileDao
    abstract fun taskDao() : TaskDao

    companion object{
        private var INSTANCE: AppDataSource? = null

        fun getProfileDatabase(context: Context): AppDataSource {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataSource::class.java,
                    "profile_data_source"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}