package com.guillaumetousignant.payingcamel.ui.trips

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.guillaumetousignant.payingcamel.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class NewTripViewModel(application: Application, initCalendar: Calendar) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val skaterRepository: SkaterRepository
    private val pathRepository: PathRepository
    private val courseRepository: CourseRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSkaters: LiveData<List<Skater>>
    val allPaths: LiveData<List<Path>>
    val allCourses: LiveData<List<Course>>

    val startCalendar: MutableLiveData<Calendar>
    val skater: MutableLiveData<Skater?>
    val path: MutableLiveData<Path?>
    val course: MutableLiveData<Course?>
    val distance: MutableLiveData<Double>

    init {
        val skaterDao = CoachRoomDatabase.getDatabase(application, scope).skaterDao()
        skaterRepository = SkaterRepository(skaterDao)
        allSkaters = skaterRepository.allSkaters

        val pathDao = CoachRoomDatabase.getDatabase(application, scope).pathDao()
        pathRepository = PathRepository(pathDao)
        allPaths = pathRepository.allPaths

        val courseDao = CoachRoomDatabase.getDatabase(application, scope).courseDao()
        courseRepository = CourseRepository(courseDao)
        allCourses = courseRepository.allCourses

        startCalendar = MutableLiveData(initCalendar)

        skater = MutableLiveData(null)
        path = MutableLiveData(null)
        course = MutableLiveData(null)
        distance = MutableLiveData(0.0)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}