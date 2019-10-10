package com.guillaumetousignant.payingcamel.database

//import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R

//import androidx.fragment.app.Fragment
//import android.content.res.Resources
//import android.app.PendingIntent.getActivity


class SkaterListAdapter internal constructor(
    //context: Context
    //inflater_in: LayoutInflater
) : RecyclerView.Adapter<SkaterListAdapter.SkaterViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var skaters = emptyList<Skater>() // Cached copy of words

    inner class SkaterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skaterItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkaterViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent, false)
        return SkaterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SkaterViewHolder, position: Int) {
        val current = skaters[position]
        //holder.skaterItemView.text = res.getString(R.string.skater_name_default,
                                        //current.first_name, current.last_name)
        //holder.skaterItemView.text = String.format(R.string.skater_name_default, current.first_name, current.last_name)
            //current.first_name + " " + current.last_name
        holder.skaterItemView.text = String.format("%s %s", current.first_name, current.last_name)
        // CHECK should use a formatted string from resources, but I have no idea on how to get it.
    }

    internal fun setSkaters(skaters: List<Skater>) {
        this.skaters = skaters
        notifyDataSetChanged()
    }

    override fun getItemCount() = skaters.size
}