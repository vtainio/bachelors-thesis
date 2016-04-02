package com.villetainio.familiarstrangers.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.estimote.sdk.*
import com.firebase.client.Firebase
import com.villetainio.familiarstrangers.util.Constants
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.adapters.BeaconListAdapter
import com.villetainio.familiarstrangers.models.CustomBeacon
import java.util.*

class RegisterBeaconActivity : AppCompatActivity() {
    val firebase = Firebase(Constants.SERVER_URL)
    var beaconManager : BeaconManager? = null
    var region: Region? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_beacon)

        beaconManager = BeaconManager(this)
        region = Region("registerBeacons", null, null, null)

        // Initialize an empty RecyclerView.
        //TODO Show text "No beacons available"
        val recyclerView = findViewById(R.id.beacon_register_list) as RecyclerView
        recyclerView.adapter = BeaconListAdapter(ArrayList<CustomBeacon>())
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        SystemRequirementsChecker.checkWithDefaultDialogs(this)
        val recyclerView = findViewById(R.id.beacon_register_list) as RecyclerView

        beaconManager?.connect{ beaconManager?.startRanging(region) }
        beaconManager?.setRangingListener { region, mutableList -> run {
            if (!mutableList.isEmpty()) {
                val beacons = ArrayList<CustomBeacon>()
                for (beacon: Beacon in mutableList) {
                    beacons.add(CustomBeacon(beacon.macAddress.toHexString(),
                            Utils.computeAccuracy(beacon)))
                }

                recyclerView.adapter = BeaconListAdapter(beacons, object: BeaconListAdapter.OnBeaconClickListener {
                    override fun onBeaconClick(macAddress: String) {
                        Toast.makeText(applicationContext, macAddress, Toast.LENGTH_LONG).show()
                    }
                })
                recyclerView.layoutManager = LinearLayoutManager(this)
            }
        } }
    }

    override fun onPause() {
        beaconManager?.stopRanging(region)
        super.onPause()
    }

    /**
     * Finish onboarding successfully
     */
    fun finishOnBoarding() {
        setResult(RESULT_OK)
        finish()
    }
}
