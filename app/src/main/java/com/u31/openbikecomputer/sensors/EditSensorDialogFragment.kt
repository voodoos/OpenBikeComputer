package com.u31.openbikecomputer.sensors

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.u31.openbikecomputer.R
import com.u31.openbikecomputer.databinding.EditSensorBinding
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText


/**
 * Dialog for scanning and displaying available BLE devices.
 */
class EditSensorDialogFragment(val sensor: Sensor, val saveCB: (Sensor) -> Unit) : DialogFragment() {
    private fun d(mess : String) { Log.d("EditSensorDialogFrag", mess) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            /* Data binding */
            val binding = EditSensorBinding.inflate(
                LayoutInflater.from(activity),
                null,
                false
            )
            binding.sensor = sensor

            /* Build the dialog */
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Edit sensor")
                .setView(binding.root)

                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    // User cancelled the dialog
                }

                .setPositiveButton(
                    R.string.save
                ) { dialog, _ ->
                    // Add sensor to DB if mac not null
                    // synthetic won't work for that use-case
                    if(dialog is AlertDialog) {
                        fun <T : TextView> getString(id : Int) : String? {
                            return dialog.findViewById<T>(id)?.text.let {
                                if(it.isNullOrBlank()) null
                                else it.toString()
                            }
                        }

                        val wheelSize = getString<EditText>(R.id.wheel_size_textEdit)?.let {Integer.valueOf(it)}

                        saveCB(Sensor(sensor.mac, getString<TextInputEditText>(R.id.name_textEdit), wheelSize))
                    }
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}