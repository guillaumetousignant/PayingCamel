package com.guillaumetousignant.payingcamel.ui.skaters

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.database.SkaterListAdapter

class SkatersFragment : Fragment() {

    private lateinit var skatersViewModel: SkatersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        skatersViewModel =
            ViewModelProviders.of(this).get(SkatersViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_skaters, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.skaters_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = SkaterListAdapter{}
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        skatersViewModel.allSkaters.observe(this, Observer { skaters ->
            // Update the cached copy of the words in the adapter.
            skaters?.let { adapter.setSkaters(it) }
        })

        val fabSkaters: FloatingActionButton = root.findViewById(R.id.fab_skaters)
        fabSkaters.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        return root
    }
}