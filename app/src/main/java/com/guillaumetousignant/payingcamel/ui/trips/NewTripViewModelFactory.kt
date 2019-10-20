package com.guillaumetousignant.payingcamel.ui.trips

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NewTripViewModelFactory(application: Application, inputCalendar: Calendar) : ViewModelProvider.Factory {

    private var mApplication = application
    private var calendar = inputCalendar

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewTripViewModel(mApplication, calendar) as T
    }
}