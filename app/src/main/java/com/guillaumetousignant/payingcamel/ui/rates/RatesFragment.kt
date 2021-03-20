package com.guillaumetousignant.payingcamel.ui.rates

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.guillaumetousignant.payingcamel.R

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.NewRateActivity
import com.guillaumetousignant.payingcamel.database.rate.Rate
import com.guillaumetousignant.payingcamel.database.rate.RateItemDetailsLookup
import com.guillaumetousignant.payingcamel.database.rate.RateItemKeyProvider
import com.guillaumetousignant.payingcamel.database.rate.RateListAdapter
import java.util.*

class RatesFragment : Fragment(R.layout.fragment_rates) {

    private lateinit var ratesViewModel: RatesViewModel
    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var keyProvider: RateItemKeyProvider
    private val actionModeCallback: ActionModeCallback = ActionModeCallback()
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ratesViewModel =
            ViewModelProvider(this).get(RatesViewModel::class.java)
        val recyclerView: RecyclerView = view.findViewById(R.id.rates_recyclerview)
        val adapter = RateListAdapter(context) {}
        recyclerView.adapter = adapter
        keyProvider = RateItemKeyProvider()
        selectionTracker = SelectionTracker.Builder(
            "rateSelection",
            recyclerView,
            keyProvider,
            RateItemDetailsLookup(recyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        adapter.tracker = selectionTracker
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        selectionTracker.addObserver(RateSelectionObserver())

        ratesViewModel.allRates.observe(viewLifecycleOwner, { rates ->
            // Update the cached copy of the words in the adapter.
            rates?.let { adapter.setRates(it)
                keyProvider.setRates(it)}
        })

        val fabRates: FloatingActionButton = view.findViewById(R.id.fab_rates)
        fabRates.setOnClickListener { /*fabView ->*/
            val intent = Intent(activity, NewRateActivity::class.java)
            startRateForResult.launch(intent)
        }
    }

    private val startRateForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val rate = Rate(
                        UUID.randomUUID(),
                        data.getIntExtra(NewRateActivity.EXTRA_AMOUNT, 0),
                        data.getStringExtra(NewRateActivity.EXTRA_NAME),
                        data.getStringExtra(NewRateActivity.EXTRA_NOTE),
                        data.getSerializableExtra(NewRateActivity.EXTRA_SKATER) as UUID?,
                        getRandomMaterialColor(getString(R.string.icon_color_type))
                    )
                    ratesViewModel.insert(rate)
                }
            }
            Activity.RESULT_CANCELED -> {
                view?.let{
                    Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
            else -> {
                view?.let{
                    Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }
    }

    private fun getRandomMaterialColor(typeColor: String): Int {
        var returnColor = Color.GRAY
        val arrayId = resources.getIdentifier("mdcolor_$typeColor", "array", activity?.packageName)

        if (arrayId != 0) {
            val colors = resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        selectionTracker.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        selectionTracker.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun enableActionMode() {
        if (actionMode == null) {
            actionMode = (activity as AppCompatActivity?)?.startSupportActionMode(actionModeCallback)
        }
        toggleSelection()
    }

    private fun toggleSelection() {
        //mAdapter.toggleSelection(position)
        val count = selectionTracker.selection.size()

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        private var statusBarColor: Int? = 0
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            statusBarColor = activity?.window?.statusBarColor
            activity?.let{
                it.window?.statusBarColor = it.getColor(R.color.colorAccent)
            }
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    // delete all the selected messages
                    val uuidList = selectionTracker.selection.toList()
                    val rateList = mutableListOf<Rate>() // CHECK maybe not best way
                    for (uuid in uuidList){
                        ratesViewModel.allRates.value?.let{
                            rateList.add(it[keyProvider.getPosition(uuid)])
                        }
                    }

                    ratesViewModel.delete(rateList)
                    mode.finish()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            statusBarColor?.let{
                activity?.window?.statusBarColor = it
            }
            selectionTracker.clearSelection()
            actionMode = null
            /*recyclerView.post(Runnable {
                mAdapter.resetAnimationIndex()
                // mAdapter.notifyDataSetChanged();
            })*/
        }
    }

    private inner class RateSelectionObserver : SelectionTracker.SelectionObserver<String>() {
        override fun onItemStateChanged(key: String, selected: Boolean) {
            super.onItemStateChanged(key, selected)
            enableActionMode()
        }
    }
}