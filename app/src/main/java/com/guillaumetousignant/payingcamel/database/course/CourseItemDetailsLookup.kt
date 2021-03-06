package com.guillaumetousignant.payingcamel.database.course

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView


class CourseItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        return view?.let {
            (recyclerView.getChildViewHolder(it) as CourseListAdapter.CourseViewHolder)
                .getItemDetails()
        }
    }
}