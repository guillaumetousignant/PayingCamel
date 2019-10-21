package com.guillaumetousignant.payingcamel.ui.paths

import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
import androidx.lifecycle.AndroidViewModel
import android.app.Application

import com.guillaumetousignant.payingcamel.database.path.PathRepository
import com.guillaumetousignant.payingcamel.database.path.Path
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PathsViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: PathRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allPaths: LiveData<List<Path>>

    init {
        val pathDao = CoachRoomDatabase.getDatabase(application, scope).pathDao()
        repository = PathRepository(pathDao)
        allPaths = repository.allPaths
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(path: Path) = scope.launch(Dispatchers.IO) {
        repository.insert(path)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}