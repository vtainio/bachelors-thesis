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

package com.villetainio.familiarstrangers

import android.app.Application
import android.preference.PreferenceManager
import com.estimote.sdk.BeaconManager
import com.estimote.sdk.Region
import com.firebase.client.Firebase
import com.estimote.sdk.Beacon
import android.util.Log
import android.widget.Toast
import com.firebase.client.DataSnapshot
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.villetainio.familiarstrangers.util.Constants

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
                for (beacon: Beacon in list) {
                    Log.d(TAG, beacon.macAddress.toHexString())
                }

                val detectedMac = list.first().macAddress.toHexString()
                val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val userId = preferences.getString(getString(R.string.settings_uid), "")
                val beaconRegistered = preferences.getBoolean(getString(R.string.settings_beacon_registerd), false)
                val ownMac = preferences.getString(getString(R.string.settings_beacon_mac), "")

                // Check that the user has registerd or logged in before storing encounters.
                if (userId.length != 0 && beaconRegistered && !detectedMac.equals(ownMac)) {
                    storeEncounter(detectedMac, firebase)
                }
            }

            override fun onExitedRegion(region: Region) {
                Log.d(TAG, "Beacon exited")
                // No need to do anything since the interaction is already stored
            }
        })
    }

    fun storeEncounter(macAddress: String, firebase: Firebase) {
        val strangerRef = firebase.child(getString(R.string.firebase_beacons))
                .child(macAddress)

        strangerRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val strangerId = snapshot.child("user").value as String
                    val strangerName = snapshot.child("name").value as String
                    storeEncounterToUsersProfile(firebase, strangerId, strangerName)
                }
            }

            override fun onCancelled(error: FirebaseError) {
                showError(error.message)
            }
        })
    }

    fun storeEncounterToUsersProfile(firebase: Firebase, id: String, name: String) {
        val userId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.settings_uid), "")
        val encounterRef = firebase.child(getString(R.string.firebase_users))
                .child(userId)
                .child(getString(R.string.firebase_users_encounters))
                .child(id)

        encounterRef.child(getString(R.string.firebase_users_encounters_name)).setValue(name)
        encounterRef.child(getString(R.string.firebase_users_encounters_amount))
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val amount = snapshot.value as Long
                            encounterRef.child(getString(R.string.firebase_users_encounters_amount))
                                    .setValue(amount + 1)
                        } else {
                            encounterRef.child(getString(R.string.firebase_users_encounters_amount))
                                .setValue(1)
                        }
                    }

                    override fun onCancelled(error: FirebaseError) {
                        showError(error.message)
                    }
                })
    }

    fun showError(error: String, length: Int = Toast.LENGTH_LONG) {
        Toast.makeText(applicationContext, error, length)
    }
}
