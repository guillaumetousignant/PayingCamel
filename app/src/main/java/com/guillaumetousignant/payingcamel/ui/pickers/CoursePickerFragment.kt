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
import com.guillaumetousignant.payingcamel.database.Course.Course
import com.guillaumetousignant.payingcamel.database.Course.CourseListAdapter

class CoursePickerFragment(val course: MutableLiveData<Course?>, private val allCourses: LiveData<List<Course>>) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.course_picker_dialog, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.course_picker_dialog_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = CourseListAdapter {
            course.postValue(it)
            dismiss()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        allCourses.observe(this, Observer { courses ->
            // Update the cached copy of the words in the adapter.
            courses?.let { adapter.setCourses(it) }
        })

        //val listener :RecyclerView.set

        return view
    }
}