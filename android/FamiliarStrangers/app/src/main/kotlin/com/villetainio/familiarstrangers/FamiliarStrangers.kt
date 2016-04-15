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
import android.location.LocationManager
import android.location.LocationListener
import android.location.Location
import android.os.Bundle
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
import com.villetainio.familiarstrangers.util.NameGenerator
import java.util.*

class FamiliarStrangers : Application() {
    val TAG = "BeaconService"
    var locationManager : LocationManager? = null

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
        val firebase = Firebase(Constants.SERVER_URL)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

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
                    val strangerSex = snapshot.child("sex").value as String
                    storeEncounterToUsersProfile(firebase, strangerId, strangerName, strangerSex)
                }
            }

            override fun onCancelled(error: FirebaseError) {
                showError(error.message)
            }
        })
    }

    fun storeEncounterToUsersProfile(firebase: Firebase, id: String, name: String, gender: String) {
        val userId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.settings_uid), "")
        val encounterRef = firebase.child(getString(R.string.firebase_users))
                .child(userId)
                .child(getString(R.string.firebase_users_encounters))
                .child(id)
        val fakename = NameGenerator().generateName(gender, applicationContext.assets)

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
                            // Set a new fake name only for the first time.
                            encounterRef.child(getString(R.string.firebase_users_encounters_fakename)).setValue(fakename)
                        }
                    }

                    override fun onCancelled(error: FirebaseError) {
                        showError(error.message)
                    }
                })

        // Store real life location of the encounter if GPS is turned on.
        val location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        // Check that the location value is recent.
        if (location != null && location.time > Calendar.getInstance().timeInMillis - 60 * 1000) {
            Log.d("EncounterLocation", String.format("lon: %.2f lat: %.2f", location.longitude, location.latitude))

            // Store the location to Firebase.
            val newLocationRef = encounterRef.child("locations").push()
            newLocationRef.child("longitude").setValue(location.longitude)
            newLocationRef.child("latitude").setValue(location.latitude)
        } else {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.toFloat(), EncounterLocationListener(encounterRef, locationManager!!))
        }
    }

    fun showError(error: String, length: Int = Toast.LENGTH_LONG) {
        Toast.makeText(applicationContext, error, length)
    }

    class EncounterLocationListener(encounterRef: Firebase, locationManager: LocationManager) : LocationListener {
        val mEncounterRef = encounterRef
        val mLocationManager = locationManager

        override fun onLocationChanged(location: Location) {
            Log.d("EncounterLocationListener", String.format("lon: %.2f lat: %.2f", location.longitude, location.latitude))

            // Store the encounter to Firebase.
            val newLocationRef = mEncounterRef.child("locations").push()
            newLocationRef.child("longitude").setValue(location.longitude)
            newLocationRef.child("latitude").setValue(location.latitude)

            mLocationManager.removeUpdates(this)
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }
}
