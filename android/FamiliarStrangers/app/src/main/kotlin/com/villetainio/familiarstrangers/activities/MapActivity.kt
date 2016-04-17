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
import android.support.v4.app.FragmentActivity
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.util.Constants

class MapActivity : FragmentActivity() {
    val firebase = Firebase(Constants.SERVER_URL)
    var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap -> onMapReady(googleMap) }
    }

    fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val userId = intent.extras.getString("userId")
        val strangerId = intent.extras.getString("strangerId")
        val locationsRef = firebase.child(getString(R.string.firebase_users))
            .child(userId)
            .child(getString(R.string.firebase_users_encounters))
            .child(strangerId)
            .child(getString(R.string.firebase_users_encounters_locations))

        locationsRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (location: DataSnapshot in snapshot.children) {
                        if (location.exists() &&
                            location.child(getString(R.string.firebase_users_encounters_location_longitude)).exists() &&
                            location.child(getString(R.string.firebase_users_encounters_location_latitude)).exists()) {
                            val latitude = location.child(getString(R.string.firebase_users_encounters_location_latitude)).value as Double
                            val longitude = location.child(getString(R.string.firebase_users_encounters_location_longitude)).value as Double

                            val marker = LatLng(latitude, longitude)
                            mMap?.addMarker(MarkerOptions().position(marker))
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
                        }
                    }
                }
            }

            override fun onCancelled(error: FirebaseError) {
                // Do nothing.
            }
        })
    }
}
