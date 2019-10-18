package com.guillaumetousignant.payingcamel.database

//import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R

//import androidx.fragment.app.Fragment


class FillListAdapter internal constructor(
    //context: Context
    //inflater_in: LayoutInflater
) : RecyclerView.Adapter<FillListAdapter.FillViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var fills = emptyList<Fill>() // Cached copy of words

    inner class FillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fillItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FillViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return FillViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FillViewHolder, position: Int) {
        val current = fills[position]
        holder.fillItemView.text = current.name?:"Default fill name"
    }

    internal fun setFills(fills: List<Fill>) {
        this.fills = fills
        notifyDataSetChanged()
    }

    override fun getItemCount() = fills.size
}