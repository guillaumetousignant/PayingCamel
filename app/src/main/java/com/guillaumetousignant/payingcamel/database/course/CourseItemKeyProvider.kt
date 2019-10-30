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

    fun getKeys(i: List<Int>): List<String?> {
        val list = mutableListOf<String?>() // CHECK maybe not best way
        for (value in i){
            list.add(courseList[value].uuid.toString())
        }
        return list
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?: RecyclerView.NO_POSITION // CHECK this is sketchy
    }

    fun getPositions(courses: List<String>): List<Int> {
        val list = mutableListOf<Int>() // CHECK maybe not best way
        for (course in courses){
            list.add(mKeyToPosition[course]?: RecyclerView.NO_POSITION)
        }
        return list
    }

    fun setCourses(newList: List<Course>){
        courseList = newList
        mKeyToPosition = HashMap(courseList.size) // CHECK maybe just modify?
        for ((index, course) in courseList.withIndex()) {
            mKeyToPosition[course.uuid.toString()] = index
        }
    }
}