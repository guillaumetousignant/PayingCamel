package com.guillaumetousignant.payingcamel.ui.gas

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
import com.guillaumetousignant.payingcamel.database.FillListAdapter

class GasFragment : Fragment() {

    private lateinit var gasViewModel: GasViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gasViewModel =
            ViewModelProviders.of(this).get(GasViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gas, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.gas_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = FillListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        gasViewModel.allFills.observe(this, Observer { fills ->
            // Update the cached copy of the words in the adapter.
            fills?.let { adapter.setFills(it) }
        })
        return root
    }
}