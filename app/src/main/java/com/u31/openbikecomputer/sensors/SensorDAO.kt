package com.u31.openbikecomputer.sensors

import androidx.room.*

@Dao
interface SensorDAO {
    @Query("SELECT * FROM sensor")
    fun getAll(): List<Sensor>

    //@Query("SELECT * FROM sensor WHERE uid IN (:userIds)")
    //fun loadAllByIds(userIds: IntArray): List<Sensor>

    @Query("SELECT * FROM sensor WHERE mac LIKE :mac LIMIT 1")
    fun findByMac(mac: String): Sensor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg sensors: Sensor)

    @Delete
    fun delete(sensor: Sensor)
}