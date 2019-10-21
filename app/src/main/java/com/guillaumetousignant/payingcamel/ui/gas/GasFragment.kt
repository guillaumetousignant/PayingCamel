package com.guillaumetousignant.payingcamel.ui.gas

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.R

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.NewFillActivity
import com.guillaumetousignant.payingcamel.database.fill.Fill
import com.guillaumetousignant.payingcamel.database.fill.FillListAdapter
import java.util.*

class GasFragment : Fragment() {

    private val newFillActivityRequestCode = 5
    private lateinit var gasViewModel: GasViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gasViewModel =
            ViewModelProviders.of(this).get(GasViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gas, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.gas_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = FillListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        gasViewModel.allFills.observe(this, Observer { fills ->
            // Update the cached copy of the words in the adapter.
            fills?.let { adapter.setFills(it) }
        })

        val fabGas: FloatingActionButton = root.findViewById(R.id.fab_gas)
        fabGas.setOnClickListener { view ->
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            val intent = Intent(activity, NewFillActivity::class.java)
            intent.putExtra(NewFillActivity.EXTRA_CALENDAR, Calendar.getInstance())
            startActivityForResult(intent, newFillActivityRequestCode)
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newFillActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val fill = Fill(
                    UUID.randomUUID(),
                    data.getIntExtra(NewFillActivity.EXTRA_AMOUNT, 0),
                    data.getSerializableExtra(NewFillActivity.EXTRA_START) as Calendar,
                    data.getStringExtra(NewFillActivity.EXTRA_NAME),
                    data.getStringExtra(NewFillActivity.EXTRA_NOTE)
                )
                gasViewModel.insert(fill)
                Unit
            }
        }
        else if (requestCode == newFillActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
            /* view?.let{
                 Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
             }*/

            /*Toast.makeText(
                context,
                R.string.cancelled,
                Toast.LENGTH_LONG
            ).show()*/
        }
        else {
            view?.let{
                Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }
}