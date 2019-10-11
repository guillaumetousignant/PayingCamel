package com.guillaumetousignant.payingcamel.ui.paths

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
import com.guillaumetousignant.payingcamel.database.PathListAdapter

class PathsFragment : Fragment() {

    private lateinit var pathsViewModel: PathsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pathsViewModel =
            ViewModelProviders.of(this).get(PathsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_paths, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.paths_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = PathListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        pathsViewModel.allPaths.observe(this, Observer { paths ->
            // Update the cached copy of the words in the adapter.
            paths?.let { adapter.setPaths(it) }
        })

        val fabPaths: FloatingActionButton = root.findViewById(R.id.fab_paths)
        fabPaths.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        return root
    }
}