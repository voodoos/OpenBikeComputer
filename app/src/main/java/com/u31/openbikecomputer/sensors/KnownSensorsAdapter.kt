package com.u31.openbikecomputer.sensors

import androidx.recyclerview.widget.DiffUtil
import com.u31.openbikecomputer.R
import com.u31.openbikecomputer.SensorsActivity
import com.u31.openbikecomputer.util.DataBindingAdapter

class KnownSensorsAdapter : DataBindingAdapter<SensorsActivity.Item>(DiffCallback()) {
    class DiffCallback : DiffUtil.ItemCallback<SensorsActivity.Item>() {
        override fun areItemsTheSame(oldItem: SensorsActivity.Item, newItem: SensorsActivity.Item): Boolean {
            return oldItem.sensor == newItem.sensor
        }

        override fun areContentsTheSame(oldItem: SensorsActivity.Item, newItem: SensorsActivity.Item): Boolean {
            // todo update when needed, or move to Sensor class
            return oldItem.sensor.mac == newItem.sensor.mac
                    && oldItem.sensor.name == newItem.sensor.mac
                    && oldItem.sensor.wheel_size == newItem.sensor.wheel_size
        }
    }

    override fun getItemViewType(position: Int) = R.layout.item_sensor
}