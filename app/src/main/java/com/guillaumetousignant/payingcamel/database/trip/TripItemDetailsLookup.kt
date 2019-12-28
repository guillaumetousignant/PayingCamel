package com.guillaumetousignant.payingcamel.database.trip

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView


class TripItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        return view?.let {
            (recyclerView.getChildViewHolder(it) as TripListAdapter.TripViewHolder)
                .getItemDetails()
        }
    }
}