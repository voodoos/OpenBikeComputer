package com.u31.openbikecomputer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.u31.openbikecomputer.sensors.Sensor
import com.u31.openbikecomputer.util.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SensorsViewModel(app: Application): AndroidViewModel(app) {
    private fun d(text : String) { Log.d("SensorsViewModel", text) }

    private val sensors: MutableLiveData<List<Sensor>> by lazy {
        MutableLiveData<List<Sensor>>().also { loadSensors() }
    }

    fun getSensors(): LiveData<List<Sensor>> {
        return sensors
    }

    private fun loadSensors() {
        // Do an asynchronous operation to fetch sensors.
        viewModelScope.launch(Dispatchers.Main) {
            // Updating sensors list in UI thread
            d("Loading known sensors from db")
            sensors.value = async(Dispatchers.IO) {
                // Fetching data in a separate IO thread
                return@async AppDatabase.get(getApplication()).sensorDao().getAll()
            }.await()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel", "Cleared !")
    }

    fun addSensor(s: Sensor) {
        // Do an asynchronous operation to add and re-fetch sensors.
        viewModelScope.launch(Dispatchers.Main) {
            // Updating sensors list in UI thread
            d("Adding (new) sensor to db")
            d(s.toString())

            sensors.value = async(Dispatchers.IO) {
                // Adding and fetching data in a separate IO thread
                val db = AppDatabase.get(getApplication())
                db.sensorDao().insertAll(s)
                return@async db.sensorDao().getAll()
            }.await()
        }
    }

    fun removeSensor(s: Sensor) {
        // Do an asynchronous operation to add and re-fetch sensors.
        viewModelScope.launch(Dispatchers.Main) {
            // Updating sensors list in UI thread
            d("Removing sensor from db")
            d(s.toString())

            sensors.value = async(Dispatchers.IO) {
                // Adding and fetching data in a separate IO thread
                val db = AppDatabase.get(getApplication())
                db.sensorDao().delete(s)
                return@async db.sensorDao().getAll()
            }.await()
        }
    }
}