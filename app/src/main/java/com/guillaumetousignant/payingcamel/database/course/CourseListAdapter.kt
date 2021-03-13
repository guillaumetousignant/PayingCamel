package com.guillaumetousignant.payingcamel.database.course

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R
import com.guillaumetousignant.payingcamel.ui.helpers.FlipAnimator
import android.widget.ImageView
import androidx.recyclerview.selection.SelectionTracker
import java.util.Locale
import androidx.cardview.widget.CardView

class CourseListAdapter internal constructor(
    val context: Context?,
    //inflater_in: LayoutInflater
    val listener: (Course) -> Unit
) : RecyclerView.Adapter<CourseListAdapter.CourseViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var courses = emptyList<Course>() // Cached copy of words
    var tracker: SelectionTracker<String>? = null

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCardView: CardView = itemView.findViewById(R.id.cardView)
        val courseItemView: TextView = itemView.findViewById(R.id.textView)
        val iconText: TextView = itemView.findViewById(R.id.icon_text)
        val imgProfile: ImageView = itemView.findViewById(R.id.icon_profile)
        val iconBack: RelativeLayout = itemView.findViewById(R.id.icon_back)
        val iconFront: RelativeLayout = itemView.findViewById(R.id.icon_front)
        val iconContainer: RelativeLayout = itemView.findViewById(R.id.icon_container)
        var uuid: String? = null

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String? = uuid
            }

        fun bind(course: Course, position: Int, isActivated: Boolean = false) {
            courseItemView.text = course.name?:"(...)"
            itemView.isActivated = isActivated
            iconText.text = course.name?.substring(0,1)?.toUpperCase(Locale.getDefault())?:"-"
            uuid = course.uuid.toString()

            applyIconAnimation(this, position, uuid)
            applyProfilePicture(this, course)
            //applyClickEvents(this, position)
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

        val selected = tracker?.isSelected(holder.uuid)?:false
        holder.bind(current, position, selected)

        holder.courseCardView.setOnClickListener {
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

    private fun applyIconAnimation(holder: CourseViewHolder, position: Int, key: String?) {
        if (tracker?.isSelected(key) == true) {
            holder.iconFront.visibility = View.GONE
            resetIconYAxis(holder.iconBack)
            holder.iconBack.visibility = View.VISIBLE
            holder.iconBack.alpha = 1.0f
            //if (currentSelectedIndex === position) {
                context?.let {
                    FlipAnimator.flipView(it, holder.iconBack, holder.iconFront, true)
                }
            //    resetCurrentIndex()
            //}
        } else {
            holder.iconBack.visibility  = View.GONE
            resetIconYAxis(holder.iconFront)
            holder.iconFront.visibility = View.VISIBLE
            holder.iconFront.alpha = 1.0f
            /*if (reverseAllAnimations && animationItemsIndex.get(
                    position,
                    false
                ) || currentSelectedIndex === position
            ) {*/
                context?.let {
                    FlipAnimator.flipView(it, holder.iconBack, holder.iconFront, false)
                }
                //resetCurrentIndex()
            //}
        }
    }

    /*private fun applyClickEvents(holder: MyViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener(View.OnClickListener {
            listener.onIconClicked(
                position
            )
        })

        holder.iconImp.setOnClickListener(View.OnClickListener {
            listener.onIconImportantClicked(
                position
            )
        })

        holder.messageContainer.setOnClickListener(View.OnClickListener {
            listener.onMessageRowClicked(
                position
            )
        })

        holder.messageContainer.setOnLongClickListener(View.OnLongClickListener { view ->
            listener.onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        })
    }*/


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    /*fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }*/

    //override fun getItemId(position: Int): Long = position.toLong()
}