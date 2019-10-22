package com.guillaumetousignant.payingcamel.ui.rates

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.R

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.NewRateActivity
import com.guillaumetousignant.payingcamel.database.rate.Rate
import com.guillaumetousignant.payingcamel.database.rate.RateListAdapter
import java.util.*

class RatesFragment : Fragment(R.layout.fragment_rates) {

    private val newRateActivityRequestCode = 7
    private lateinit var ratesViewModel: RatesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ratesViewModel =
            ViewModelProviders.of(this).get(RatesViewModel::class.java)
        val recyclerView: RecyclerView = view.findViewById(R.id.rates_recyclerview)
        val adapter = RateListAdapter {}
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        ratesViewModel.allRates.observe(this, Observer { rates ->
            // Update the cached copy of the words in the adapter.
            rates?.let { adapter.setRates(it) }
        })

        val fabRates: FloatingActionButton = view.findViewById(R.id.fab_rates)
        fabRates.setOnClickListener { /*fabView ->*/
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            val intent = Intent(activity, NewRateActivity::class.java)
            startActivityForResult(intent, newRateActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newRateActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val rate = Rate(
                    UUID.randomUUID(),
                    data.getIntExtra(NewRateActivity.EXTRA_AMOUNT, 0),
                    data.getStringExtra(NewRateActivity.EXTRA_NAME),
                    data.getStringExtra(NewRateActivity.EXTRA_NOTE),
                    data.getSerializableExtra(NewRateActivity.EXTRA_SKATER) as UUID?
                )
                ratesViewModel.insert(rate)
                Unit
            }
        }
        else if (requestCode == newRateActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
            /* view?.let{
                 Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
             }*/
        }
        else {
            view?.let{
                Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }
}