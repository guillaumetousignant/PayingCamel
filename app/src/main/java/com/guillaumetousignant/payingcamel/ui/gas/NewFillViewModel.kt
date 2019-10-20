package com.guillaumetousignant.payingcamel.ui.gas

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class NewFillViewModel(application: Application, initCalendar: Calendar) : AndroidViewModel(application) {

    val startCalendar = MutableLiveData(initCalendar)

}