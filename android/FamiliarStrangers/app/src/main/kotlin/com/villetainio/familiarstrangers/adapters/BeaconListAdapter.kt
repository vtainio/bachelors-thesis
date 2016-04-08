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

package com.villetainio.familiarstrangers.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import java.util.*
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.models.CustomBeacon

class BeaconListAdapter(beacons: ArrayList<CustomBeacon>, listener: OnBeaconClickListener? = null) : RecyclerView.Adapter<BeaconListAdapter.CustomViewHolder>() {
    val mBeacons = beacons
    var mListener: OnBeaconClickListener? = listener

    interface OnBeaconClickListener {
        fun onBeaconClick(macAddress: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CustomViewHolder {
        val infl = LayoutInflater.from(parent.context)
        var view = infl.inflate(R.layout.item_beacons, parent, false)

        return CustomViewHolder(view, object: CustomViewHolder.OnViewClickListener {
            override fun onViewClick(v: View, adapterPosition: Int) {
                mListener?.onBeaconClick(mBeacons.get(adapterPosition).identifier)
            }
        })
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val beacon = mBeacons.get(position)
        holder.beaconIdentifier.text = beacon.identifier
        holder.beaconDistance.text = String.format("%.2fm", beacon.distance)
    }

    override fun getItemCount() : Int {
        return mBeacons.size
    }


    /**
     * ViewHolder implementation.
     */
    open class CustomViewHolder : RecyclerView.ViewHolder {
        var mListener: OnViewClickListener? = null
        val beaconIdentifier = itemView.findViewById(R.id.beaconIdentifier) as TextView
        val beaconDistance = itemView.findViewById(R.id.beaconDistance) as TextView

        interface OnViewClickListener {
            fun onViewClick(v: View, adapterPosition: Int)
        }

        constructor(itemView: View, listener: OnViewClickListener): super(itemView) {
            mListener = listener
            beaconIdentifier.setOnClickListener(onClickListener)
            beaconDistance.setOnClickListener(onClickListener)
        }

        val onClickListener = View.OnClickListener() { view -> mListener?.onViewClick(view, adapterPosition) }
    }
}
