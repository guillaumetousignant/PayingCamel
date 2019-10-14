package com.guillaumetousignant.payingcamel.ui.overview

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
import com.guillaumetousignant.payingcamel.database.CourseListAdapter

//import com.google.android.material.snackbar.Snackbar
//import kotlinx.android.synthetic.main.fragment_overview.fab_overview
import android.content.Intent
import android.app.Activity
import android.widget.Toast
import com.guillaumetousignant.payingcamel.NewCourseActivity
import com.guillaumetousignant.payingcamel.database.Course
import java.util.*

class OverviewFragment : Fragment() {

    private val newCourseActivityRequestCode = 1
    private lateinit var overviewViewModel: OverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        overviewViewModel =
            ViewModelProviders.of(this).get(OverviewViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_overview, container, false)

        val recyclerView: RecyclerView = root.findViewById(R.id.overview_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = CourseListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        overviewViewModel.allCourses.observe(this, Observer { courses ->
            // Update the cached copy of the words in the adapter.
            courses?.let { adapter.setCourses(it) }
        })

        val fabOverview: FloatingActionButton = root.findViewById(R.id.fab_overview)
        /*fabOverview.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        fabOverview.setOnClickListener {
            val intent = Intent(activity, NewCourseActivity::class.java)
            startActivityForResult(intent, newCourseActivityRequestCode)
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newCourseActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val course = Course(UUID.randomUUID(), data.getStringExtra(NewCourseActivity.EXTRA_REPLY))
                overviewViewModel.insert(course)
                Unit
            }
        } else {
            Toast.makeText(
                context,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}