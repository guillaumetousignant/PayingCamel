package com.guillaumetousignant.payingcamel.database.course

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CourseDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from course_table ORDER BY start_time DESC")
    fun getDescCourses(): LiveData<List<Course>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(course: Course)


    @Query("DELETE FROM course_table")
    fun deleteAll()

    @Delete
    fun delete(courses: List<Course>)
}