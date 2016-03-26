package com.villetainio.familiarstrangers.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import java.util.*
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.models.Encounter

class EncountersAdapter(encounters: ArrayList<Encounter>) : RecyclerView.Adapter<EncountersAdapter.CustomViewHolder>() {
    val mEncounters = encounters

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CustomViewHolder {
        val infl = LayoutInflater.from(parent.context)
        var view = infl.inflate(R.layout.item_encounters, parent, false)
        return CustomViewHolder(view)
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
        constructor(itemView: View): super(itemView) {}
        val personName = itemView.findViewById(R.id.personName) as TextView
        val timesOfEncounters = itemView.findViewById(R.id.timesOfEcounters) as TextView
    }
}
