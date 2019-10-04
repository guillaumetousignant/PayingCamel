package com.guillaumetousignant.payingcamel.ui.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.R

class RatesFragment : Fragment() {

    private lateinit var ratesViewModel: RatesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ratesViewModel =
            ViewModelProviders.of(this).get(RatesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_rates, container, false)
        val textView: TextView = root.findViewById(R.id.text_rates)
        ratesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}