package com.u31.openbikecomputer.sensors

import androidx.recyclerview.widget.DiffUtil
import com.u31.openbikecomputer.R
import com.u31.openbikecomputer.util.DataBindingAdapter

class KnownSensorsAdapter : DataBindingAdapter<SensorsFragment.Item>(DiffCallback()) {
    class DiffCallback : DiffUtil.ItemCallback<SensorsFragment.Item>() {
        override fun areItemsTheSame(oldItem: SensorsFragment.Item, newItem: SensorsFragment.Item): Boolean {
            return oldItem.sensor == newItem.sensor
        }

        override fun areContentsTheSame(oldItem: SensorsFragment.Item, newItem: SensorsFragment.Item): Boolean {
            // todo update when needed, or move to Sensor class
            return oldItem.sensor.mac == newItem.sensor.mac
                    && oldItem.sensor.name == newItem.sensor.mac
                    && oldItem.sensor.wheel_size == newItem.sensor.wheel_size
        }
    }

    override fun getItemViewType(position: Int) = R.layout.item_sensor
}