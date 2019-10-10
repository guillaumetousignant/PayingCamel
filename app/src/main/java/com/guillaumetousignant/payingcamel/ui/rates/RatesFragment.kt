package com.guillaumetousignant.payingcamel.ui.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.R

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.database.RateListAdapter

class RatesFragment : Fragment() {

    private lateinit var ratesViewModel: RatesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ratesViewModel =
            ViewModelProviders.of(this).get(RatesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_rates, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.rates_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = RateListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(getActivity()) // CHECK can return null

        ratesViewModel.allRates.observe(this, Observer { rates ->
            // Update the cached copy of the words in the adapter.
            rates?.let { adapter.setRates(it) }
        })
        return root
    }
}