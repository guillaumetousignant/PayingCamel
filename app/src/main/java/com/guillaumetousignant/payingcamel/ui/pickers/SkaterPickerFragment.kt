package com.guillaumetousignant.payingcamel.ui.pickers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.database.skater.SkaterListAdapter

class SkaterPickerFragment(val skater: MutableLiveData<Skater?>, private val allSkaters: LiveData<List<Skater>>) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.skater_picker_dialog, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.skater_picker_dialog_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = SkaterListAdapter {
            skater.postValue(it)
            dismiss()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        allSkaters.observe(this, Observer { skaters ->
            // Update the cached copy of the words in the adapter.
            skaters?.let { adapter.setSkaters(it) }
        })

        //val listener :RecyclerView.set

        return view
    }
}