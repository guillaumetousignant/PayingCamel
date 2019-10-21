package com.guillaumetousignant.payingcamel.ui.rates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import com.guillaumetousignant.payingcamel.database.Skater.Skater
import com.guillaumetousignant.payingcamel.database.Skater.SkaterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class NewRateViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val skaterRepository: SkaterRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSkaters: LiveData<List<Skater>>

    val skater: MutableLiveData<Skater?>

    init {
        val skaterDao = CoachRoomDatabase.getDatabase(application, scope).skaterDao()
        skaterRepository =
            SkaterRepository(skaterDao)
        allSkaters = skaterRepository.allSkaters

        skater = MutableLiveData(null)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}