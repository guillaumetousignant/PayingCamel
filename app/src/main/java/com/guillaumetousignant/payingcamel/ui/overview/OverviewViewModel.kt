package com.guillaumetousignant.payingcamel.ui.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.guillaumetousignant.payingcamel.database.CourseRepository
import com.guillaumetousignant.payingcamel.database.Course
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
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
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allCourses: LiveData<List<Course>>

    init {
        val courseDao = CoachRoomDatabase.getDatabase(application, scope).courseDao()
        repository = CourseRepository(courseDao)
        allCourses = repository.allCourses
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(course: Course) = scope.launch(Dispatchers.IO) {
        repository.insert(course)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}