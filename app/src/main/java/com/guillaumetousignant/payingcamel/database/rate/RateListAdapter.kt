package com.guillaumetousignant.payingcamel.database.rate

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

class RateListAdapter internal constructor(
    val context: Context?,
    //inflater_in: LayoutInflater
    val listener: (Rate) -> Unit
) : RecyclerView.Adapter<RateListAdapter.RateViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var rates = emptyList<Rate>() // Cached copy of words
    var tracker: SelectionTracker<String>? = null

    inner class RateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rateCardView: CardView = itemView.findViewById(R.id.cardView)
        val rateItemView: TextView = itemView.findViewById(R.id.textView)
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

        fun bind(rate: Rate, position: Int, isActivated: Boolean = false) {
            rateItemView.text = String.format(context?.getString(R.string.title_default)?:"%s", rate.name?:context?.getString(R.string.name_empty)?:"(...)")
            itemView.isActivated = isActivated
            iconText.text = rate.name?.substring(0,1)?.uppercase(Locale.getDefault())?:"-"
            uuid = rate.uuid.toString()

            applyIconAnimation(this, position, uuid)
            applyProfilePicture(this, rate)
            //applyClickEvents(this, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return RateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        val current = rates[position]
        val selected = tracker?.isSelected(holder.uuid)?:false
        holder.bind(current, position, selected)

        holder.rateCardView.setOnClickListener {
            listener(current)
        }
    }

    internal fun setRates(rates: List<Rate>) {
        this.rates = rates
        notifyDataSetChanged()
    }

    override fun getItemCount() = rates.size

    private fun applyProfilePicture(holder: RateViewHolder, rate: Rate) {
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
        holder.imgProfile.setColorFilter(rate.color)
        holder.iconText.visibility  = View.VISIBLE
        //}
    }

    private fun applyIconAnimation(holder: RateViewHolder, position: Int, key: String?) {
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