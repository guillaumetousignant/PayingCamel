package com.guillaumetousignant.payingcamel.ui.new_course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import com.guillaumetousignant.payingcamel.database.SkaterRepository
import com.guillaumetousignant.payingcamel.database.Skater
import com.guillaumetousignant.payingcamel.database.RateRepository
import com.guillaumetousignant.payingcamel.database.Rate
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
//import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewCourseViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val skaterRepository: SkaterRepository
    private val rateRepository: RateRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSkaters: LiveData<List<Skater>>
    val allRates: LiveData<List<Rate>>

    val startCalendar: MutableLiveData<Calendar>
    val endCalendar: MutableLiveData<Calendar>
    val skater: MutableLiveData<Skater?>
    val rate: MutableLiveData<Rate?>

    init {
        val skaterDao = CoachRoomDatabase.getDatabase(application, scope).skaterDao()
        skaterRepository = SkaterRepository(skaterDao)
        allSkaters = skaterRepository.allSkaters

        val rateDao = CoachRoomDatabase.getDatabase(application, scope).rateDao()
        rateRepository = RateRepository(rateDao)
        allRates = rateRepository.allRates

        startCalendar = MutableLiveData(Calendar.getInstance())
        val endCalendarTemp: Calendar = startCalendar.value?.clone() as Calendar
        endCalendarTemp.add(Calendar.HOUR, 1)
        endCalendar = MutableLiveData(endCalendarTemp)

        skater = MutableLiveData(null)
        rate = MutableLiveData(null)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}