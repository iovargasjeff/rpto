package com.example.roomdatabase.application

import android.app.Application
import androidx.room.Room
import com.example.roomdatabase.data.database.AppDatabase

class RoomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    companion object {
        private const val DATABASE_NAME = "database-users"

        lateinit var db: AppDatabase
            private set

        lateinit var instance: RoomApplication
            private set
    }
}
