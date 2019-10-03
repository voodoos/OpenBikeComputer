package com.u31.openbikecomputer

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.u31.openbikecomputer.sensors.EditSensorDialogFragment
import com.u31.openbikecomputer.sensors.FindBTLEDialogFragment
import com.u31.openbikecomputer.sensors.KnownSensorsAdapter
import com.u31.openbikecomputer.sensors.Sensor
import kotlinx.android.synthetic.main.fragment_sensors.*

class SensorsFragment : Fragment() {
    private lateinit var model: SensorsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: KnownSensorsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val data : MutableList<Item> = mutableListOf()

    private fun d(mess : String) { Log.d("SensorsFragment", mess) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /*********
         * MODEL *
         *********/

        // Use appropritate ViewModel to update list
        model = ViewModelProviders.of(this)[SensorsViewModel::class.java]


        /******
         * UI *
         ******/

        /* Recyclier, listing already known sensors */
        viewManager = LinearLayoutManager(activity)
        viewAdapter = KnownSensorsAdapter().also { it.submitList(data) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sensors, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /******
         * UI *
         ******/

        view?.let {
            recyclerView = it.findViewById<RecyclerView>(R.id.sensors_recycler_view).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                //setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter
            }
        }

        /****************/
        /* MODEL <-> UI */
        /****************/

        /* Update data from model */
        model.getSensors().observe(this, Observer<List<Sensor>>{ sensors ->
            // update UI
            data.clear()
            data.addAll(sensors.map { Item(it) })
            recyclerView.adapter?.notifyDataSetChanged()
        })

        /* Bottom right button starts BT scan */
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        fab.setOnClickListener {
            activity?.let {
                FindBTLEDialogFragment(bluetoothAdapter, ::launchEditDialog)
                    .show(it.supportFragmentManager, "FindBTLEDialogFragment")
            }
        }
    }

    /*************
     * UTILITIES *
     *************/
    private fun launchEditDialog(sensor : Sensor) {
        activity?.let {
            EditSensorDialogFragment(sensor, model::addSensor)
                .show(it.supportFragmentManager, "EditSensorFragment")
        }
    }

    /*****************************
     * This inner class is used  *
     * for the view databindings *
     * It wraps a Sensor with    *
     * other useful bindings     *
     * such as callbacks         *
     *****************************/
    inner class Item(val sensor: Sensor) {
        // Required for DataBinging to work
        fun  onClickEdit() {
            launchEditDialog(sensor)
        }

        fun  onClickDelete() {
            //todo add warning ?
            model.removeSensor(sensor)
        }
    }
}
