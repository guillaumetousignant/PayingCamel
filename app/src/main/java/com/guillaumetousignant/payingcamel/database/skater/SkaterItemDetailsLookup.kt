package com.guillaumetousignant.payingcamel.database.skater

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView


class SkaterItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        return view?.let {
            (recyclerView.getChildViewHolder(it) as SkaterListAdapter.SkaterViewHolder)
                .getItemDetails()
        }
    }
}