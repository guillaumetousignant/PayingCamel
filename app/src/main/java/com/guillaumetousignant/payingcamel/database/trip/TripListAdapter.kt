package com.guillaumetousignant.payingcamel.database.trip

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R
import com.guillaumetousignant.payingcamel.ui.helpers.FlipAnimator
import java.util.Locale

class TripListAdapter internal constructor(
    val context: Context?,
    //inflater_in: LayoutInflater
    val listener: (Trip) -> Unit
) : RecyclerView.Adapter<TripListAdapter.TripViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var trips = emptyList<Trip>() // Cached copy of words
    var tracker: SelectionTracker<String>? = null

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tripCardView: CardView = itemView.findViewById(R.id.cardView)
        val tripItemView: TextView = itemView.findViewById(R.id.textView)
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

        fun bind(trip: Trip, position: Int, isActivated: Boolean = false) {
            tripItemView.text = String.format(context?.getString(R.string.title_default)?:"%s", trip.name?:context?.getString(R.string.name_empty)?:"(...)")
            itemView.isActivated = isActivated
            iconText.text = trip.name?.substring(0,1)?.toUpperCase(Locale.getDefault())?:"-"
            uuid = trip.uuid.toString()

            applyIconAnimation(this, position, uuid)
            applyProfilePicture(this, trip)
            //applyClickEvents(this, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return TripViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val current = trips[position]
        val selected = tracker?.isSelected(holder.uuid)?:false
        holder.bind(current, position, selected)

        holder.tripCardView.setOnClickListener {
            listener(current)
        }
    }

    internal fun setTrips(trips: List<Trip>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    override fun getItemCount() = trips.size

    private fun applyProfilePicture(holder: TripViewHolder, trip: Trip) {
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
        holder.imgProfile.setColorFilter(trip.color)
        holder.iconText.visibility  = View.VISIBLE
        //}
    }

    private fun applyIconAnimation(holder: TripViewHolder, position: Int, key: String?) {
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

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }
}