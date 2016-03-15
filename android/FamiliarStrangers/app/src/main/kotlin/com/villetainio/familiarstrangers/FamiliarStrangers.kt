package com.villetainio.familiarstrangers

import android.app.Application
import com.estimote.sdk.BeaconManager
import com.estimote.sdk.Region
import com.firebase.client.Firebase
import com.estimote.sdk.Beacon
import android.util.Log
import com.villetainio.familiarstrangers.util.Constants
import java.util.*

class FamiliarStrangers : Application() {
    val TAG = "BeaconService"

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
        val firebase = Firebase(Constants.SERVER_URL)

        val beaconManager = BeaconManager(applicationContext)

        // Connect to every beacon
        beaconManager.connect { beaconManager.startMonitoring(Region("all beacons", null, null, null)) }

        // Monitor for Enter and Exit events.
        beaconManager.setMonitoringListener(object: BeaconManager.MonitoringListener {
            override fun onEnteredRegion(region: Region, list: List<Beacon>) {
                Log.d(TAG, "Beacon found");

                // Store encounter to Firebase
                storeEncounter(list.first().macAddress.toHexString(), firebase)
            }

            override fun onExitedRegion(region: Region) {
                Log.d(TAG, "Beacon exited")
                // No need to do anything since the interaction is already stored
            }
        })
    }

    fun storeEncounter(macAddress: String, firebase: Firebase) {
        //TODO store encounter to Firebase based on the beacon mac address.
    }
}
