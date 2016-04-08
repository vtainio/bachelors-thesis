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
import com.villetainio.familiarstrangers.models.Encounter

class EncountersAdapter(encounters: ArrayList<Encounter>, listener: OnEncounterClickListener? = null) : RecyclerView.Adapter<EncountersAdapter.CustomViewHolder>() {
    val mEncounters = encounters
    var mListener: OnEncounterClickListener? = listener

    interface OnEncounterClickListener {
        fun onEncounterClick(userId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CustomViewHolder {
        val infl = LayoutInflater.from(parent.context)
        var view = infl.inflate(R.layout.item_encounters, parent, false)
        return CustomViewHolder(view, object: CustomViewHolder.OnViewClickListener {
            override fun onViewClick(v: View, adapterPosition: Int) {
                mListener?.onEncounterClick(mEncounters.get(adapterPosition).userId)
            }
        })
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val encounter = mEncounters.get(position)
        holder.personName.text = encounter.name
        holder.timesOfEncounters.text = encounter.times.toString()
    }

    override fun getItemCount() : Int {
        return mEncounters.size
    }

    open class CustomViewHolder : RecyclerView.ViewHolder {
        var mListener: OnViewClickListener? = null
        val personName = itemView.findViewById(R.id.personName) as TextView
        val timesOfEncounters = itemView.findViewById(R.id.timesOfEcounters) as TextView

        interface OnViewClickListener {
            fun onViewClick(v: View, adapterPosition: Int)
        }

        constructor(itemView: View, listener: OnViewClickListener): super(itemView) {
            mListener = listener
            personName.setOnClickListener(onClickListener)
            timesOfEncounters.setOnClickListener(onClickListener)
        }

        val onClickListener = View.OnClickListener() { view -> mListener?.onViewClick(view, adapterPosition) }
    }
}
