package com.guillaumetousignant.payingcamel.ui.overview

import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.guillaumetousignant.payingcamel.database.course.CourseRepository
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.database.skater.SkaterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class OverviewViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: CourseRepository
    private val skaterRepository: SkaterRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allCourses: LiveData<List<Course>>
    val allSkaters: LiveData<List<Skater>>

    val skater: MutableLiveData<Skater?> // Make plural!
    val startCalendar: MutableLiveData<Calendar>
    val endCalendar: MutableLiveData<Calendar>

    init {
        val courseDao = CoachRoomDatabase.getDatabase(application, scope).courseDao()
        repository = CourseRepository(courseDao)
        allCourses = repository.allCourses

        val skaterDao = CoachRoomDatabase.getDatabase(application, scope).skaterDao()
        skaterRepository = SkaterRepository(skaterDao)
        allSkaters = skaterRepository.allSkaters

        skater = MutableLiveData(null)

        val endCalendarTemp: Calendar = Calendar.getInstance()
        endCalendarTemp.set(Calendar.MILLISECOND, 999)
        endCalendarTemp.set(Calendar.SECOND, 59)
        endCalendarTemp.set(Calendar.MINUTE, 59)
        endCalendarTemp.set(Calendar.HOUR_OF_DAY, 23)
        val startCalendarTemp: Calendar = endCalendarTemp.clone() as Calendar
        startCalendarTemp.set(Calendar.DAY_OF_YEAR, 1)

        startCalendar = MutableLiveData(startCalendarTemp)
        endCalendar = MutableLiveData(endCalendarTemp)
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(course: Course) = scope.launch(Dispatchers.IO) {
        repository.insert(course)
    }

    fun delete(courses: List<Course>) = scope.launch(Dispatchers.IO) {
        repository.delete(courses)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}