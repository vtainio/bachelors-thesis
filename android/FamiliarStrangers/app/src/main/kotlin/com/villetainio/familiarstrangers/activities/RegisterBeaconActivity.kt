/*
* Copyright 2016 Ville Tainio
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.villetainio.familiarstrangers.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
                        // Store the beacon's Mac address locally.
                        PreferenceManager.getDefaultSharedPreferences(applicationContext)
                            .edit()
                            .putString(getString(R.string.settings_beacon_mac), macAddress)
                            .apply()
                        storeBeaconToFirebase(macAddress)
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
     * Save beacon to Firebase.
     */
    fun storeBeaconToFirebase(macAddress: String) {
        //TODO Check if the beacon already exists and prevent storing in that situation.
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val userId = preferences.getString(getString(R.string.settings_uid), "")
        val name = preferences.getString(getString(R.string.settings_full_name), "")

        val beaconRef = firebase.child(getString(R.string.firebase_beacons)).child(macAddress)
        beaconRef.child(getString(R.string.firebase_beacons_user)).setValue(userId)
        beaconRef.child(getString(R.string.firebase_beacons_name)).setValue(name)
        finishBeaconRegister()
    }

    /**
     * Finish beacon registering successfully
     */
    fun finishBeaconRegister() {
        // Store information that a beacon has been registered to the user.
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putBoolean(getString(R.string.settings_beacon_registerd), true)
            .apply()
        setResult(RESULT_OK)
        beaconManager?.stopRanging(region)
        finish()
    }
}
