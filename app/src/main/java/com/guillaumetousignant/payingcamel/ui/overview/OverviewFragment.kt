package com.guillaumetousignant.payingcamel.ui.overview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.R

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.guillaumetousignant.payingcamel.database.course.CourseListAdapter

import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.app.Activity
import android.graphics.Color
import android.icu.util.Calendar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.guillaumetousignant.payingcamel.NewCourseActivity
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.course.CourseItemDetailsLookup
import com.guillaumetousignant.payingcamel.database.course.CourseItemKeyProvider
import java.util.*

class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private val newCourseActivityRequestCode = 1
    private lateinit var overviewViewModel: OverviewViewModel
    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var keyProvider: CourseItemKeyProvider
    private val actionModeCallback: ActionModeCallback = ActionModeCallback()
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overviewViewModel =
            ViewModelProviders.of(this).get(OverviewViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.overview_recyclerview)
        val adapter = CourseListAdapter(context) {}
        recyclerView.adapter = adapter
        keyProvider = CourseItemKeyProvider()
        selectionTracker = SelectionTracker.Builder<String>(
            "courseSelection",
            recyclerView,
            keyProvider,
            CourseItemDetailsLookup(recyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        adapter.tracker = selectionTracker
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null


        selectionTracker.addObserver(CourseSelectionObserver())

        overviewViewModel.allCourses.observe(this, Observer { courses ->
            // Update the cached copy of the words in the adapter.
            courses?.let { adapter.setCourses(it)
                            keyProvider.setCourses(it)}
        })

        val fabOverview: FloatingActionButton = view.findViewById(R.id.fab_overview)
        fabOverview.setOnClickListener {
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
            val intent = Intent(activity, NewCourseActivity::class.java)
            intent.putExtra(NewCourseActivity.EXTRA_CALENDAR, Calendar.getInstance())
            startActivityForResult(intent, newCourseActivityRequestCode)
        }
    }

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
                    data.getBooleanExtra(NewCourseActivity.EXTRA_PAID, false),
                    getRandomMaterialColor(getString(R.string.icon_color_type))
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
        }
        else {
            view?.let{
                Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    private fun getRandomMaterialColor(typeColor: String): Int {
        var returnColor = Color.GRAY
        val arrayId = resources.getIdentifier("mdcolor_$typeColor", "array", activity?.packageName)

        if (arrayId != 0) {
            val colors = resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        selectionTracker.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        selectionTracker.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun enableActionMode() {
        if (actionMode == null) {
            actionMode = (activity as AppCompatActivity).startSupportActionMode(actionModeCallback)
        }
        toggleSelection()
    }

    private fun toggleSelection() {
        //mAdapter.toggleSelection(position)
        val count = selectionTracker.selection.size()

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    // delete all the selected messages
                    val uuidList = selectionTracker.selection.toList()
                    val courseList = mutableListOf<Course>() // CHECK maybe not best way
                    for (uuid in uuidList){
                        overviewViewModel.allCourses.value?.let{
                            courseList.add(it[keyProvider.getPosition(uuid)])
                        }
                    }

                    overviewViewModel.delete(courseList)
                    mode.finish()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            selectionTracker.clearSelection()
            actionMode = null
            /*recyclerView.post(Runnable {
                mAdapter.resetAnimationIndex()
                // mAdapter.notifyDataSetChanged();
            })*/
        }
    }

    private inner class CourseSelectionObserver : SelectionTracker.SelectionObserver<String>() {
        override fun onItemStateChanged(key: String, selected: Boolean) {
            super.onItemStateChanged(key, selected)
            enableActionMode()
        }
    }
}