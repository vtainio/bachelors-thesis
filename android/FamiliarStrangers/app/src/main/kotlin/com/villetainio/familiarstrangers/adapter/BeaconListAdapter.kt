package com.villetainio.familiarstrangers.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import java.util.*
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.models.CustomBeacon

class BeaconListAdapter(beacons: ArrayList<CustomBeacon>) : RecyclerView.Adapter<BeaconListAdapter.CustomViewHolder>() {
    val mBeacons = beacons

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CustomViewHolder {
        val infl = LayoutInflater.from(parent.context)
        var view = infl.inflate(R.layout.item_beacons, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val beacon = mBeacons.get(position)
        holder.beaconIdentifier.text = beacon.identifier
        holder.beaconDistance.text = String.format("%.2fm", beacon.distance)
    }

    override fun getItemCount() : Int {
        return mBeacons.size
    }

    open class CustomViewHolder : RecyclerView.ViewHolder {
        constructor(itemView: View): super(itemView) {}
        val beaconIdentifier = itemView.findViewById(R.id.beaconIdentifier) as TextView
        val beaconDistance = itemView.findViewById(R.id.beaconDistance) as TextView
    }
}
