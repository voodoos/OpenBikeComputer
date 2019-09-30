package com.u31.openbikecomputer.sensors

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sensor (
    @PrimaryKey val mac: String,
    val name: String?,
    val wheel_size: Int?
) {
    constructor(mac: String, name: String?)
    : this(mac, name, 2096)

    override fun toString(): String {
        val name = name ?: "Unnamed device"
        return "$name ($mac)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Sensor
        return mac == other.mac
    }

    override fun hashCode(): Int {
        return mac.hashCode()
    }
}

