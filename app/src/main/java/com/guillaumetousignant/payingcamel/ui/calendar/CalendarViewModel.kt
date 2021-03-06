package com.guillaumetousignant.payingcamel.ui.calendar

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.course.CourseRepository
import com.guillaumetousignant.payingcamel.database.expense.Expense
import com.guillaumetousignant.payingcamel.database.expense.ExpenseRepository
import com.guillaumetousignant.payingcamel.database.fill.Fill
import com.guillaumetousignant.payingcamel.database.fill.FillRepository
import com.guillaumetousignant.payingcamel.database.trip.Trip
import com.guillaumetousignant.payingcamel.database.trip.TripRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val courseRepository: CourseRepository
    private val tripRepository: TripRepository
    private val expenseRepository: ExpenseRepository
    private val fillRepository: FillRepository

    val allCourses: LiveData<List<Course>>
    val allTrips: LiveData<List<Trip>>
    val allExpenses: LiveData<List<Expense>>
    val allFills: LiveData<List<Fill>>

    val calendar: Calendar = Calendar.getInstance()

    init {
        val courseDao = CoachRoomDatabase.getDatabase(application, scope).courseDao()
        courseRepository = CourseRepository(courseDao)
        allCourses = courseRepository.allCourses
        val tripDao = CoachRoomDatabase.getDatabase(application, scope).tripDao()
        tripRepository = TripRepository(tripDao)
        allTrips = tripRepository.allTrips
        val expenseDao = CoachRoomDatabase.getDatabase(application, scope).expenseDao()
        expenseRepository = ExpenseRepository(expenseDao)
        allExpenses = expenseRepository.allExpenses
        val fillDao = CoachRoomDatabase.getDatabase(application, scope).fillDao()
        fillRepository = FillRepository(fillDao)
        allFills = fillRepository.allFills
    }

    fun insert(course: Course) = scope.launch(Dispatchers.IO) {
        courseRepository.insert(course)
    }

    fun insert(trip: Trip) = scope.launch(Dispatchers.IO) {
        tripRepository.insert(trip)
    }

    fun insert(expense: Expense) = scope.launch(Dispatchers.IO) {
        expenseRepository.insert(expense)
    }

    fun insert(fill: Fill) = scope.launch(Dispatchers.IO) {
        fillRepository.insert(fill)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}