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
import com.guillaumetousignant.payingcamel.database.Rate.Rate
import com.guillaumetousignant.payingcamel.database.Rate.RateListAdapter

class RatePickerFragment(val rate: MutableLiveData<Rate?>, private val allRates: LiveData<List<Rate>>) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.rate_picker_dialog, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rate_picker_dialog_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = RateListAdapter {
            rate.postValue(it)
            dismiss()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        allRates.observe(this, Observer { rates ->
            // Update the cached copy of the words in the adapter.
            rates?.let { adapter.setRates(it) }
        })

        //val listener :RecyclerView.set

        return view
    }
}