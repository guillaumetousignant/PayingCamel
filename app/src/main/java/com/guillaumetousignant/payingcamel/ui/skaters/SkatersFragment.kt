package com.guillaumetousignant.payingcamel.ui.skaters

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
import com.guillaumetousignant.payingcamel.NewSkaterActivity
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.database.skater.SkaterListAdapter
import java.util.*

class SkatersFragment : Fragment(R.layout.fragment_skaters) {

    private val newSkaterActivityRequestCode = 2
    private lateinit var skatersViewModel: SkatersViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skatersViewModel =
            ViewModelProviders.of(this).get(SkatersViewModel::class.java)
        val recyclerView: RecyclerView = view.findViewById(R.id.skaters_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = SkaterListAdapter {}
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        skatersViewModel.allSkaters.observe(this, Observer { skaters ->
            // Update the cached copy of the words in the adapter.
            skaters?.let { adapter.setSkaters(it) }
        })

        val fabSkaters: FloatingActionButton = view.findViewById(R.id.fab_skaters)
        fabSkaters.setOnClickListener { /*fabView ->*/
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            val intent = Intent(activity, NewSkaterActivity::class.java)
            startActivityForResult(intent, newSkaterActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newSkaterActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val skater = Skater(
                    UUID.randomUUID(),
                    data.getStringExtra(NewSkaterActivity.EXTRA_FIRST_NAME) ?: "",
                    data.getStringExtra(NewSkaterActivity.EXTRA_LAST_NAME) ?: "",
                    data.getStringExtra(NewSkaterActivity.EXTRA_NOTE),
                    data.getStringExtra(NewSkaterActivity.EXTRA_EMAIL),
                    data.getBooleanExtra(NewSkaterActivity.EXTRA_ACTIVE, true)
                )
                skatersViewModel.insert(skater)
                Unit
            }
        }
        else if (requestCode == newSkaterActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
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