package com.example.studyretrofitgson

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LocationsAdapter(val clickListener: LocationClickListener) :
    RecyclerView.Adapter<LocationViewHolder>() {

    var locations = ArrayList<ForecastLocation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
        holder.itemView.setOnClickListener { clickListener.onLocationClick(locations[position]) }
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    fun interface LocationClickListener {
        fun onLocationClick(location: ForecastLocation)
    }
}
