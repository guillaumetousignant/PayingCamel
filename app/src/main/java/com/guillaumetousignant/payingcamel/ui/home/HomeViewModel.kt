package com.guillaumetousignant.payingcamel.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the calendar Fragment"
    }
    val text: LiveData<String> = _text
}