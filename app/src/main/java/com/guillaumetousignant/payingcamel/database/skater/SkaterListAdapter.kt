package com.guillaumetousignant.payingcamel.database.skater

//import android.content.Context
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

//import androidx.fragment.app.Fragment
//import android.content.res.Resources
//import android.app.PendingIntent.getActivity


class SkaterListAdapter internal constructor(
    val context: Context?,
    //inflater_in: LayoutInflater
    val listener: (Skater) -> Unit
) : RecyclerView.Adapter<SkaterListAdapter.SkaterViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var skaters = emptyList<Skater>() // Cached copy of words
    var tracker: SelectionTracker<String>? = null

    inner class SkaterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skaterCardView: CardView = itemView.findViewById(R.id.cardView)
        val skaterItemView: TextView = itemView.findViewById(R.id.textView)
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

        fun bind(skater: Skater, position: Int, isActivated: Boolean = false) {
            skaterItemView.text = String.format(context?.getString(R.string.skater_name_default)?:"%s %s", skater.first_name, skater.last_name)
            itemView.isActivated = isActivated
            iconText.text = skater.first_name.substring(0,1).uppercase(Locale.getDefault())
            uuid = skater.uuid.toString()

            applyIconAnimation(this, position, uuid)
            applyProfilePicture(this, skater)
            //applyClickEvents(this, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkaterViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.skater_item, parent, false)
        return SkaterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SkaterViewHolder, position: Int) {
        val current = skaters[position]
        //holder.skaterItemView.text = res.getString(R.string.skater_name_default,
                                        //current.first_name, current.last_name)
        //holder.skaterItemView.text = String.format(R.string.skater_name_default, current.first_name, current.last_name)
            //current.first_name + " " + current.last_name
        //holder.skaterItemView.text = String.format(context?.getString(R.string.skater_name_default)?:"%s %s", current.first_name, current.last_name)
        // CHECK should use a formatted string from resources, but I have no idea on how to get it.

        val selected = tracker?.isSelected(holder.uuid)?:false
        holder.bind(current, position, selected)

        holder.skaterCardView.setOnClickListener {
            listener(current)
        }
    }

    internal fun setSkaters(skaters: List<Skater>) {
        this.skaters = skaters
        notifyDataSetChanged()
    }

    override fun getItemCount() = skaters.size

    private fun applyProfilePicture(holder: SkaterViewHolder, skater: Skater) {
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
        holder.imgProfile.setColorFilter(skater.color)
        holder.iconText.visibility  = View.VISIBLE
        //}
    }

    private fun applyIconAnimation(holder: SkaterViewHolder, position: Int, key: String?) {
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