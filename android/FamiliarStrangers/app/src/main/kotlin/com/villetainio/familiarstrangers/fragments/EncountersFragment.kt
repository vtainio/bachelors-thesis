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

package com.villetainio.familiarstrangers.fragments

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Toast
import com.firebase.client.*
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.activities.EncounterActivity
import com.villetainio.familiarstrangers.adapters.EncountersAdapter
import com.villetainio.familiarstrangers.models.Encounter
import com.villetainio.familiarstrangers.util.Constants
import java.util.ArrayList

class EncountersFragment : Fragment() {
    val firebase = Firebase(Constants.SERVER_URL)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_encounters, container, false)

        val recyclerView = view.findViewById(R.id.fragment_encounters) as RecyclerView
        recyclerView.adapter = EncountersAdapter(ArrayList<Encounter>())
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    override fun onResume() {
        super.onResume()

        val recyclerView = view?.findViewById(R.id.fragment_encounters) as RecyclerView
        getEncounterReference().addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val encountersList = ArrayList<Encounter>()

                for (encounter: DataSnapshot in snapshot.children) {
                    if (encounter.exists() &&
                            encounter.child(getString(R.string.firebase_users_encounters_fakename)).exists() &&
                            encounter.child(getString(R.string.firebase_users_encounters_amount)).exists()) {
                        encountersList.add(
                                Encounter(
                                        encounter.child(getString(R.string.firebase_users_encounters_fakename)).value as String,
                                        encounter.child(getString(R.string.firebase_users_encounters_amount)).value as Long,
                                        encounter.key
                                )
                        )
                    }
                }

                recyclerView.adapter = EncountersAdapter(encountersList, object: EncountersAdapter.OnEncounterClickListener {
                    override fun onEncounterClick(userId: String) {

                        // Start a new activity for the selected stranger.
                        val profileIntent = Intent(activity, EncounterActivity::class.java)
                        profileIntent.putExtra("userId", userId)
                        startActivity(profileIntent)
                    }
                })
                recyclerView.layoutManager = LinearLayoutManager(activity)
            }

            override fun onCancelled(p0: FirebaseError?) {
                // Display an error message.
                Toast.makeText(context, getString(R.string.error_default), Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getEncounterReference() : Query {
        val userId = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(getString(R.string.settings_uid), "")
        return firebase.child(getString(R.string.firebase_users))
                .child(userId)
                .child(getString(R.string.firebase_users_encounters))
    }
}
