package com.guillaumetousignant.payingcamel.ui.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToolsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the paths Fragment"
    }
    val text: LiveData<String> = _text
}