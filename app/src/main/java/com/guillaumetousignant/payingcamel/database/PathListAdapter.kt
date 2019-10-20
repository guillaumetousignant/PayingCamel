package com.guillaumetousignant.payingcamel.database

//import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R

//import androidx.fragment.app.Fragment


class PathListAdapter internal constructor(
    //context: Context
    //inflater_in: LayoutInflater
    val listener: (Path) -> Unit
) : RecyclerView.Adapter<PathListAdapter.PathViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var paths = emptyList<Path>() // Cached copy of words

    inner class PathViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pathItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return PathViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        val current = paths[position]
        holder.pathItemView.text = current.name?:"(...)"

        holder.pathItemView.setOnClickListener {
            listener(current)
        }
    }

    internal fun setPaths(paths: List<Path>) {
        this.paths = paths
        notifyDataSetChanged()
    }

    override fun getItemCount() = paths.size
}