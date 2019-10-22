package com.guillaumetousignant.payingcamel.ui.trips

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
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
import com.guillaumetousignant.payingcamel.NewTripActivity
import com.guillaumetousignant.payingcamel.database.trip.Trip
import com.guillaumetousignant.payingcamel.database.trip.TripListAdapter
import java.util.*

class TripsFragment : Fragment(R.layout.fragment_trips) {

    private val newTripActivityRequestCode = 3
    private lateinit var tripsViewModel: TripsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripsViewModel =
            ViewModelProviders.of(this).get(TripsViewModel::class.java)
        val recyclerView: RecyclerView = view.findViewById(R.id.trips_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = TripListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        tripsViewModel.allTrips.observe(this, Observer { expenses ->
            // Update the cached copy of the words in the adapter.
            expenses?.let { adapter.setTrips(it) }
        })

        val fabTrips: FloatingActionButton = view.findViewById(R.id.fab_trips)
        fabTrips.setOnClickListener { /*fabView ->*/
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            val intent = Intent(activity, NewTripActivity::class.java)
            intent.putExtra(NewTripActivity.EXTRA_CALENDAR, Calendar.getInstance())
            startActivityForResult(intent, newTripActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newTripActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val trip = Trip(
                    UUID.randomUUID(),
                    data.getStringExtra(NewTripActivity.EXTRA_PATH),
                    data.getStringExtra(NewTripActivity.EXTRA_FROM),
                    data.getStringExtra(NewTripActivity.EXTRA_TO),
                    data.getDoubleExtra(NewTripActivity.EXTRA_DISTANCE, 0.0),
                    data.getSerializableExtra(NewTripActivity.EXTRA_START) as Calendar,
                    data.getSerializableExtra(NewTripActivity.EXTRA_COURSE) as UUID?,
                    data.getSerializableExtra(NewTripActivity.EXTRA_SKATER) as UUID?,
                    data.getStringExtra(NewTripActivity.EXTRA_NAME),
                    data.getStringExtra(NewTripActivity.EXTRA_NOTE)
                )
                tripsViewModel.insert(trip)
                Unit
            }
        }
        else if (requestCode == newTripActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
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