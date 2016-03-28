package com.villetainio.familiarstrangers.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.adapter.EncountersAdapter
import com.villetainio.familiarstrangers.models.Encounter
import java.util.ArrayList

class EncountersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_encounters, container, false)

        val encounters = ArrayList<Encounter>()
        encounters.add(Encounter("Ville", 2))

        val recyclerView = view.findViewById(R.id.fragment_encounters) as RecyclerView
        recyclerView.adapter = EncountersAdapter(encounters)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }
}