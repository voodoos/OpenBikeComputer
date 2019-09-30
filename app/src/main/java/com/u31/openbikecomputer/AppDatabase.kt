package com.u31.openbikecomputer

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.u31.openbikecomputer.sensors.Sensor
import com.u31.openbikecomputer.sensors.SensorDAO

@Database(entities = arrayOf(Sensor::class), version = 1)
abstract class ABAppDatabase : RoomDatabase() {
    abstract fun sensorDao(): SensorDAO
}

object AppDatabase {
    // Inspired by https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
    @Volatile private lateinit var db : ABAppDatabase
    fun get(applicationContext : Context) : ABAppDatabase {
        if(AppDatabase::db.isInitialized) {
            return db
        }

        return synchronized(this) {
            if (!AppDatabase::db.isInitialized) {
                Log.d("AppDatabase", "New instance created")
                val created = Room.databaseBuilder(
                    applicationContext,
                    ABAppDatabase::class.java, "database-name"
                ).build()
                db = created
            }
            db
        }
    }
}