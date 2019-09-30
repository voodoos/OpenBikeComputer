package com.u31.openbikecomputer.sensors

import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.u31.openbikecomputer.R

/**
 * Dialog for scanning and displaying available BLE devices.
 */
class FindBTLEDialogFragment(bluetoothAdapter: BluetoothAdapter?, val addSensorCB: (Sensor) -> Unit) : DialogFragment() {
    private fun d(text : String) { Log.d("FindBTLEDialogFragment", text) }
    private val scanner =  bluetoothAdapter?.bluetoothLeScanner
    private val sensors = mutableSetOf<Sensor>()

    private lateinit var sensorsAdapter: ArrayAdapter<Sensor>

    private val scannCb = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let {
                sensors.add(Sensor(it.address, it.name))

                sensorsAdapter.clear()
                sensorsAdapter.addAll(sensors)
                sensorsAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun startScan() {
        val filter = ScanFilter.Builder()
            //.setServiceUuid(ParcelUuid.fromString("00001816-0000-1000-8000-00805F9B34FB"))
            .build()
        val settings = ScanSettings.Builder().build()
        scanner?.startScan(listOf(filter), settings, scannCb) // check nullity
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sensorsAdapter = ArrayAdapter(context, R.layout.found_sensor)

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.btle_search)
                .setAdapter(sensorsAdapter)
                { _, i -> sensorsAdapter.getItem(i)?.let { addSensorCB(it)} }
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { _dialog, _id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onResume() {
        super.onResume()
        Log.d("FindBTLEDialogFragment", "Start scan")
        startScan()
    }

    override fun onPause() {
        super.onPause()
        Log.d("FindBTLEDialogFragment", "Stop scan")
        scanner?.stopScan(scannCb)
    }


}