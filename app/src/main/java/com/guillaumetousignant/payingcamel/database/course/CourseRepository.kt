package com.guillaumetousignant.payingcamel.database.course

import android.icu.util.Calendar
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.guillaumetousignant.payingcamel.database.skater.Skater

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class CourseRepository(private val courseDao: CourseDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allCourses: LiveData<List<Course>> = courseDao.getDescCourses()

    fun getDatedCourses(startCalendar: Calendar, endCalendar: Calendar): LiveData<List<Course>> {
        return courseDao.getDatedCourses(startCalendar, endCalendar)
    }

    fun getDatedSkatersCourses(startCalendar: Calendar, endCalendar: Calendar, skaters: List<Skater>): LiveData<List<Course>> {
        return courseDao.getDatedSkatersCourses(startCalendar, endCalendar, skaters.map{it.uuid})
    }

    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(course: Course) {
        courseDao.insert(course)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(courses: List<Course>) {
        courseDao.delete(courses)
    }
}