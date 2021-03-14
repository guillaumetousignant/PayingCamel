package com.guillaumetousignant.payingcamel.ui.overview

import androidx.lifecycle.LiveData
import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.guillaumetousignant.payingcamel.database.course.CourseRepository
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import com.guillaumetousignant.payingcamel.database.expense.Expense
import com.guillaumetousignant.payingcamel.database.expense.ExpenseRepository
import com.guillaumetousignant.payingcamel.database.fill.Fill
import com.guillaumetousignant.payingcamel.database.fill.FillRepository
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.database.skater.SkaterRepository
import com.guillaumetousignant.payingcamel.database.trip.Trip
import com.guillaumetousignant.payingcamel.database.trip.TripRepository
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
    private val expenseRepository: ExpenseRepository
    private val fillRepository: FillRepository
    private val tripRepository: TripRepository

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
    val trips: MutableLiveData<List<Trip>> = MutableLiveData(emptyList())
    val tripsAmount: MutableLiveData<Double> = MutableLiveData(0.0)
    val expenses: MutableLiveData<List<Expense>> = MutableLiveData(emptyList())
    val expensesAmount: MutableLiveData<Int> = MutableLiveData(0)
    val fills: MutableLiveData<List<Fill>> = MutableLiveData(emptyList())
    val fillsAmount: MutableLiveData<Int> = MutableLiveData(0)

    init {
        val courseDao = CoachRoomDatabase.getDatabase(application, scope).courseDao()
        repository = CourseRepository(courseDao)

        val skaterDao = CoachRoomDatabase.getDatabase(application, scope).skaterDao()
        skaterRepository = SkaterRepository(skaterDao)
        allSkaters = skaterRepository.allSkaters

        val expenseDao = CoachRoomDatabase.getDatabase(application, scope).expenseDao()
        expenseRepository = ExpenseRepository(expenseDao)

        val fillDao = CoachRoomDatabase.getDatabase(application, scope).fillDao()
        fillRepository =FillRepository(fillDao)

        val tripDao = CoachRoomDatabase.getDatabase(application, scope).tripDao()
        tripRepository = TripRepository(tripDao)

        skaters = MutableLiveData(emptyList())

        val endCalendarTemp: Calendar = Calendar.getInstance()
        endCalendarTemp.set(Calendar.MILLISECOND, 999)
        endCalendarTemp.set(Calendar.SECOND, 59)
        endCalendarTemp.set(Calendar.MINUTE, 59)
        endCalendarTemp.set(Calendar.HOUR_OF_DAY, 23)
        val startCalendarTemp: Calendar = Calendar.getInstance()
        startCalendarTemp.set(Calendar.DAY_OF_YEAR, 1)
        startCalendarTemp.set(Calendar.MILLISECOND, 0)
        startCalendarTemp.set(Calendar.SECOND, 0)
        startCalendarTemp.set(Calendar.MINUTE, 0)
        startCalendarTemp.set(Calendar.HOUR_OF_DAY, 24)

        startCalendar = MutableLiveData(startCalendarTemp)
        endCalendar = MutableLiveData(endCalendarTemp)
    }

    fun fetchCourses() = scope.launch(Dispatchers.IO) {
        if (skaters.value?.isEmpty() != false) {
            courses.postValue(repository.getDatedCourses(startCalendar.value ?: Calendar.getInstance(), endCalendar.value ?: Calendar.getInstance()))
        }
        else {
            courses.postValue(repository.getDatedSkatersCourses(startCalendar.value ?: Calendar.getInstance(), endCalendar.value ?: Calendar.getInstance(), skaters.value ?: emptyList()))
        }
    }

    fun fetchTrips() = scope.launch(Dispatchers.IO) {
        if (skaters.value?.isEmpty() != false) {
            trips.postValue(tripRepository.getDatedTrips(startCalendar.value ?: Calendar.getInstance(), endCalendar.value ?: Calendar.getInstance()))
        }
        else {
            trips.postValue(tripRepository.getDatedSkatersTrips(startCalendar.value ?: Calendar.getInstance(), endCalendar.value ?: Calendar.getInstance(), skaters.value ?: emptyList()))
        }
    }

    fun fetchExpenses() = scope.launch(Dispatchers.IO) {
        expenses.postValue(expenseRepository.getDatedExpenses(startCalendar.value ?: Calendar.getInstance(), endCalendar.value ?: Calendar.getInstance()))
    }

    fun fetchFills() = scope.launch(Dispatchers.IO) {
        fills.postValue(fillRepository.getDatedFills(startCalendar.value ?: Calendar.getInstance(), endCalendar.value ?: Calendar.getInstance()))
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