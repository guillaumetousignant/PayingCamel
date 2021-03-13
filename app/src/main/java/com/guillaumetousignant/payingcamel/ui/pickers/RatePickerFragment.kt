package com.guillaumetousignant.payingcamel.ui.pickers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R
import com.guillaumetousignant.payingcamel.database.rate.Rate
import com.guillaumetousignant.payingcamel.database.rate.RateListAdapter

class RatePickerFragment(val rate: MutableLiveData<Rate?>, private val allRates: LiveData<List<Rate>>) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.rate_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.rate_picker_dialog_recyclerview)
        val adapter = RateListAdapter(context) {
            rate.postValue(it)
            dismiss()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        allRates.observe(this, { rates ->
            // Update the cached copy of the words in the adapter.
            rates?.let { adapter.setRates(it) }
        })
    }
}