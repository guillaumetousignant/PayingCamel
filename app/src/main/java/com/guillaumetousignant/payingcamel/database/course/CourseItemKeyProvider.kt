package com.guillaumetousignant.payingcamel.database.course

import androidx.recyclerview.selection.ItemKeyProvider

class CourseItemKeyProvider internal constructor(private val courseList: List<Course>) :
    ItemKeyProvider<String>(SCOPE_CACHED) {

    private val mKeyToPosition: MutableMap<String, Int>

    init {
        mKeyToPosition = HashMap(courseList.size)
        for ((index, course) in courseList.withIndex()) {
            mKeyToPosition[course.uuid.toString()] = index
        }
    }

    override fun getKey(i: Int): String? {
        return courseList[i].uuid.toString()
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?:0 // CHECK this is sketchy
    }
}