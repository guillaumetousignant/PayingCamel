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
    val allSkaters: LiveData<List<Skater>>

    val skaters: MutableLiveData<List<Skater>> // Make plural!
    val startCalendar: MutableLiveData<Calendar>
    val endCalendar: MutableLiveData<Calendar>
    val courses: MutableLiveData<List<Course>> = MutableLiveData(emptyList())
    val amount: MutableLiveData<Int> = MutableLiveData(0)

    init {
        val courseDao = CoachRoomDatabase.getDatabase(application, scope).courseDao()
        repository = CourseRepository(courseDao)

        val skaterDao = CoachRoomDatabase.getDatabase(application, scope).skaterDao()
        skaterRepository = SkaterRepository(skaterDao)
        allSkaters = skaterRepository.allSkaters

        skaters = MutableLiveData(emptyList())

        val endCalendarTemp: Calendar = Calendar.getInstance()
        endCalendarTemp.set(Calendar.MILLISECOND, 999)
        endCalendarTemp.set(Calendar.SECOND, 59)
        endCalendarTemp.set(Calendar.MINUTE, 59)
        endCalendarTemp.set(Calendar.HOUR_OF_DAY, 23)
        val startCalendarTemp: Calendar = Calendar.getInstance()
        startCalendarTemp.set(Calendar.DAY_OF_YEAR, 1)
        endCalendarTemp.set(Calendar.MILLISECOND, 0)
        endCalendarTemp.set(Calendar.SECOND, 0)
        endCalendarTemp.set(Calendar.MINUTE, 0)
        endCalendarTemp.set(Calendar.HOUR_OF_DAY, 24)

        startCalendar = MutableLiveData(startCalendarTemp)
        endCalendar = MutableLiveData(endCalendarTemp)

        fetchCourses()
    }

    fun fetchCourses() {
        if (skaters.value?.isEmpty() != false) {
            courses.postValue(repository.getDatedCourses(startCalendar.value ?: Calendar.getInstance(), endCalendar.value ?: Calendar.getInstance()).value)
        }
        else {
            courses.postValue(repository.getDatedSkatersCourses(startCalendar.value ?: Calendar.getInstance(), endCalendar.value ?: Calendar.getInstance(), skaters.value ?: emptyList()).value)
        }
        var amountTemp = 0
        courses.value?.let {
            for (course in it) {
                amountTemp += course.amount
            }
        }
        amount.postValue(amountTemp)
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