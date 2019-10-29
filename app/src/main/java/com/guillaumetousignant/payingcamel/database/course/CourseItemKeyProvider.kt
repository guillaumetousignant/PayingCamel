package com.guillaumetousignant.payingcamel.database.course

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class CourseItemKeyProvider internal constructor() :
    ItemKeyProvider<String>(SCOPE_CACHED) {

    private var mKeyToPosition: MutableMap<String, Int> = HashMap()
    private var courseList: List<Course> = emptyList()

    override fun getKey(i: Int): String? {
        return courseList[i].uuid.toString()
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?: RecyclerView.NO_POSITION // CHECK this is sketchy
    }

    fun setCourses(newList: List<Course>){
        courseList = newList
        mKeyToPosition = HashMap(courseList.size) // CHECK maybe just modify?
        for ((index, course) in courseList.withIndex()) {
            mKeyToPosition[course.uuid.toString()] = index
        }
    }
}