package com.guillaumetousignant.payingcamel.database.course

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R
import java.util.*

class CourseListAdapter internal constructor(
    //context: Context
    //inflater_in: LayoutInflater
    val listener: (Course) -> Unit
) : RecyclerView.Adapter<CourseListAdapter.CourseViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var courses = emptyList<Course>() // Cached copy of words

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseItemView: TextView = itemView.findViewById(R.id.textView)
        val firstLetterView: TextView = itemView.findViewById(R.id.first_letter)
        val firstLetterCircle: RelativeLayout = itemView.findViewById(R.id.first_letter_circle)
        lateinit var uuid: String

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String? = uuid
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val current = courses[position]
        holder.courseItemView.text = current.name?:"(...)"
        holder.firstLetterView.text = current.name?.substring(0,1)?.toUpperCase(Locale.getDefault())?:"-"
        holder.uuid = current.uuid.toString()

        holder.courseItemView.setOnClickListener {
            listener(current)
        }
    }

    internal fun setCourses(courses: List<Course>) {
        this.courses = courses
        notifyDataSetChanged()
    }

    override fun getItemCount() = courses.size

    //override fun getItemId(position: Int): Long = position.toLong()
}