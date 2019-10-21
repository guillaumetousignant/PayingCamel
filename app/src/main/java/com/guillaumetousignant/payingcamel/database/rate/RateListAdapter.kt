package com.guillaumetousignant.payingcamel.database.rate

//import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R

//import androidx.fragment.app.Fragment


class RateListAdapter internal constructor(
    //context: Context
    //inflater_in: LayoutInflater
    val listener: (Rate) -> Unit
) : RecyclerView.Adapter<RateListAdapter.RateViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var rates = emptyList<Rate>() // Cached copy of words

    inner class RateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rateItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return RateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        val current = rates[position]
        holder.rateItemView.text = current.name?:"(..)"

        holder.rateItemView.setOnClickListener {
            listener(current)
        }
    }

    internal fun setRates(rates: List<Rate>) {
        this.rates = rates
        notifyDataSetChanged()
    }

    override fun getItemCount() = rates.size
}