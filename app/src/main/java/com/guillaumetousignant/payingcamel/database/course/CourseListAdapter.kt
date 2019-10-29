package com.guillaumetousignant.payingcamel.database.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R
import com.guillaumetousignant.payingcamel.ui.helpers.FlipAnimator
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.guillaumetousignant.payingcamel.ui.helpers.CircleTransform
import com.bumptech.glide.Glide
import android.text.TextUtils
import android.widget.ImageView
import java.util.Locale

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

        //val firstLetterView: TextView = itemView.findViewById(R.id.first_letter)
        //val firstLetterCircle: RelativeLayout = itemView.findViewById(R.id.first_letter_circle)

        val iconText: TextView = itemView.findViewById(R.id.icon_text)
        val imgProfile: ImageView = itemView.findViewById(R.id.icon_profile)
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


    // CHECK added
    private fun applyProfilePicture(holder: CourseViewHolder, course: Course) {
        /*if (!TextUtils.isEmpty(message.getPicture())) {
            Glide.with(mContext).load(message.getPicture())
                .thumbnail(0.5f)
                .crossFade()
                .transform(CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgProfile)
            holder.imgProfile.setColorFilter(null)
            holder.iconText.setVisibility(View.GONE)
        } else {*/
            holder.imgProfile.setImageResource(R.drawable.circle_shape_grey)
            holder.imgProfile.setColorFilter(course.color)
            holder.iconText.visibility  = View.VISIBLE
        //}
    }

    private fun applyIconAnimation(holder: CourseViewHolder, position: Int) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE)
            resetIconYAxis(holder.iconBack)
            holder.iconBack.setVisibility(View.VISIBLE)
            holder.iconBack.setAlpha(1)
            if (currentSelectedIndex === position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true)
                resetCurrentIndex()
            }
        } else {
            holder.iconBack.setVisibility(View.GONE)
            resetIconYAxis(holder.iconFront)
            holder.iconFront.setVisibility(View.VISIBLE)
            holder.iconFront.setAlpha(1)
            if (reverseAllAnimations && animationItemsIndex.get(
                    position,
                    false
                ) || currentSelectedIndex === position
            ) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false)
                resetCurrentIndex()
            }
        }
    }


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    //override fun getItemId(position: Int): Long = position.toLong()
}