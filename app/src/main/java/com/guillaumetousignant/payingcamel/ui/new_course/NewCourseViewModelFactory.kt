package com.guillaumetousignant.payingcamel.ui.new_course

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import android.app.Application
import android.icu.util.Calendar


class NewCourseViewModelFactory(application: Application, inputCalendar: Calendar) : ViewModelProvider.Factory {

    private var mApplication = application
    private var calendar = inputCalendar

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewCourseViewModel(mApplication, calendar) as T
    }
}