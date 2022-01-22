package com.guillaumetousignant.payingcamel.database.fill

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

class FillListAdapter internal constructor(
    val context: Context?,
    //inflater_in: LayoutInflater
    val listener: (Fill) -> Unit
) : RecyclerView.Adapter<FillListAdapter.FillViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var fills = emptyList<Fill>() // Cached copy of words
    var tracker: SelectionTracker<String>? = null

    inner class FillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fillCardView: CardView = itemView.findViewById(R.id.cardView)
        val fillItemView: TextView = itemView.findViewById(R.id.textView)
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

        fun bind(fill: Fill, position: Int, isActivated: Boolean = false) {
            fillItemView.text = String.format(context?.getString(R.string.title_default)?:"%s", fill.name?:context?.getString(R.string.name_empty)?:"(...)")
            itemView.isActivated = isActivated
            iconText.text = fill.name?.substring(0,1)?.uppercase(Locale.getDefault())?:"-"
            uuid = fill.uuid.toString()

            applyIconAnimation(this, position, uuid)
            applyProfilePicture(this, fill)
            //applyClickEvents(this, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FillViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return FillViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FillViewHolder, position: Int) {
        val current = fills[position]

        val selected = tracker?.isSelected(holder.uuid)?:false
        holder.bind(current, position, selected)

        holder.fillCardView.setOnClickListener {
            listener(current)
        }
    }

    internal fun setFills(fills: List<Fill>) {
        this.fills = fills
        notifyDataSetChanged()
    }

    override fun getItemCount() = fills.size

    private fun applyProfilePicture(holder: FillViewHolder, fill: Fill) {
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
        holder.imgProfile.setColorFilter(fill.color)
        holder.iconText.visibility  = View.VISIBLE
        //}
    }

    private fun applyIconAnimation(holder: FillViewHolder, position: Int, key: String?) {
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