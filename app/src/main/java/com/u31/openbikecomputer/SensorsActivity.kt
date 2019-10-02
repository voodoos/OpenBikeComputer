package com.u31.openbikecomputer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.u31.openbikecomputer.sensors.EditSensorDialogFragment
import com.u31.openbikecomputer.sensors.FindBTLEDialogFragment
import com.u31.openbikecomputer.sensors.KnownSensorsAdapter
import com.u31.openbikecomputer.sensors.Sensor
import kotlinx.android.synthetic.main.activity_sensors.*

class SensorsActivity : FragmentActivity() {
    private lateinit var model: SensorsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: KnownSensorsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val data : MutableList<Item> = mutableListOf()

    private val MY_PERMISSIONS_REQUEST_LOCATION = 200

    private fun d(mess : String) { Log.d("SensorsActivity", mess) }



    /*************
     * LIFECYCLE *
     *************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setSupportActionBar(toolbar)

        setContentView(R.layout.activity_sensors)


        /*********
         * MODEL *
         *********/

        // Use appropritate ViewModel to update list
        model = ViewModelProviders.of(this)[SensorsViewModel::class.java]

        /******
         * UI *
         ******/

        /* Recyclier, listing already known sensors */
        viewManager = LinearLayoutManager(this)
        viewAdapter = KnownSensorsAdapter().also { it.submitList(data) }

        recyclerView = findViewById<RecyclerView>(R.id.sensors_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            //setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
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
            FindBTLEDialogFragment(bluetoothAdapter, ::launchEditDialog)
                .show(supportFragmentManager, "FindBTLEDialogFragment")
        }



        /***************
         * PERMISSIONS *
         ***************/
        // Check and request permissions
        // todo move elsewhere when elsewhere will exist
        if (ContextCompat.checkSelfPermission(this@SensorsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            d("ACCESS_FINE_LOCATION location not granted")
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this@SensorsActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION)
        } else {
            d("ACCESS_FINE_LOCATION location already granted")
        }
        d("test")
        // end check permissions

    }

    // todo move
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        d("Request permission results")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    d("Granted")
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    d("Denied")
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*************
     * UTILITIES *
     *************/
    private fun launchEditDialog(sensor : Sensor) {
        EditSensorDialogFragment(sensor, model::addSensor)
            .show(supportFragmentManager, "EditSensorFragment")
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
