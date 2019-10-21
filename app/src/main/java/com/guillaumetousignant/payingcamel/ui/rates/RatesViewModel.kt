package com.guillaumetousignant.payingcamel.ui.rates

import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
import androidx.lifecycle.AndroidViewModel
import android.app.Application

import com.guillaumetousignant.payingcamel.database.Rate.RateRepository
import com.guillaumetousignant.payingcamel.database.Rate.Rate
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RatesViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: RateRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allRates: LiveData<List<Rate>>

    init {
        val rateDao = CoachRoomDatabase.getDatabase(application, scope).rateDao()
        repository = RateRepository(rateDao)
        allRates = repository.allRates
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(rate: Rate) = scope.launch(Dispatchers.IO) {
        repository.insert(rate)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}