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
import com.guillaumetousignant.payingcamel.database.course.CourseListAdapter

import com.google.android.material.snackbar.Snackbar
//import kotlinx.android.synthetic.main.fragment_overview.fab_overview
import android.content.Intent
import android.app.Activity
import android.icu.util.Calendar
//import android.widget.Toast
import com.guillaumetousignant.payingcamel.NewCourseActivity
import com.guillaumetousignant.payingcamel.database.course.Course
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
        val adapter = CourseListAdapter {}
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        overviewViewModel.allCourses.observe(this, Observer { courses ->
            // Update the cached copy of the words in the adapter.
            courses?.let { adapter.setCourses(it) }
        })

        val fabOverview: FloatingActionButton = root.findViewById(R.id.fab_overview)
        fabOverview.setOnClickListener {
            //openDialog()
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
            val intent = Intent(activity, NewCourseActivity::class.java)
            intent.putExtra(NewCourseActivity.EXTRA_CALENDAR, Calendar.getInstance())
            startActivityForResult(intent, newCourseActivityRequestCode)
        }

        return root
    }

    /*private fun openDialog() {
        fragmentManager?.let {
            NewCourseDialog.display(it)
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newCourseActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val course = Course(
                    UUID.randomUUID(),
                    data.getSerializableExtra(NewCourseActivity.EXTRA_SKATER) as UUID?,
                    data.getSerializableExtra(NewCourseActivity.EXTRA_START) as Calendar,
                    data.getSerializableExtra(NewCourseActivity.EXTRA_END) as Calendar,
                    data.getIntExtra(NewCourseActivity.EXTRA_RATE, 0),
                    data.getIntExtra(NewCourseActivity.EXTRA_AMOUNT, 0),
                    data.getStringExtra(NewCourseActivity.EXTRA_NAME),
                    data.getStringExtra(NewCourseActivity.EXTRA_NOTE),
                    data.getBooleanExtra(NewCourseActivity.EXTRA_PAID, false)
                )
                overviewViewModel.insert(course)
                Unit
            }
        }
        else if (requestCode == newCourseActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
           /* view?.let{
                Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }*/

            /*Toast.makeText(
                context,
                R.string.cancelled,
                Toast.LENGTH_LONG
            ).show()*/
        }
        else {
            view?.let{
                Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }
}