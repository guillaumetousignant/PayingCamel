package com.guillaumetousignant.payingcamel.database.course

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.room.*
import com.guillaumetousignant.payingcamel.database.skater.Skater
import java.util.*

@Dao
interface CourseDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from course_table ORDER BY start_time DESC")
    fun getDescCourses(): LiveData<List<Course>>

    @Query("SELECT * from course_table WHERE start_time >= :startCalendar AND end_time <= :endCalendar ORDER BY start_time ASC")
    fun getDatedCourses(startCalendar: Calendar, endCalendar: Calendar): List<Course>

    @Query("SELECT * from course_table WHERE start_time >= :startCalendar AND end_time <= :endCalendar AND skater IN (:skaters) ORDER BY start_time ASC")
    fun getDatedSkatersCourses(startCalendar: Calendar, endCalendar: Calendar, skaters: List<UUID>): List<Course>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(course: Course)

    @Query("DELETE FROM course_table")
    fun deleteAll()

    @Delete
    fun delete(courses: List<Course>)
}