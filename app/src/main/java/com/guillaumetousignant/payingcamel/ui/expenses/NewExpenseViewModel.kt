package com.guillaumetousignant.payingcamel.ui.expenses

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class NewExpenseViewModel(application: Application, initCalendar: Calendar) : AndroidViewModel(application) {

    val startCalendar = MutableLiveData(initCalendar)

}