package com.guillaumetousignant.payingcamel.ui.rates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RatesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is rates Fragment"
    }
    val text: LiveData<String> = _text
}