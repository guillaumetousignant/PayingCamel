package com.guillaumetousignant.payingcamel.ui.gas

import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
import androidx.lifecycle.AndroidViewModel
import android.app.Application

import com.guillaumetousignant.payingcamel.database.FillRepository
import com.guillaumetousignant.payingcamel.database.Fill
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GasViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: FillRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allFills: LiveData<List<Fill>>

    init {
        val fillDao = CoachRoomDatabase.getDatabase(application, scope).fillDao()
        repository = FillRepository(fillDao)
        allFills = repository.allFills
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(fill: Fill) = scope.launch(Dispatchers.IO) {
        repository.insert(fill)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}