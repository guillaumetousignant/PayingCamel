package com.guillaumetousignant.payingcamel.ui.gas

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
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
import com.guillaumetousignant.payingcamel.NewFillActivity
import com.guillaumetousignant.payingcamel.database.fill.Fill
import com.guillaumetousignant.payingcamel.database.fill.FillItemDetailsLookup
import com.guillaumetousignant.payingcamel.database.fill.FillItemKeyProvider
import com.guillaumetousignant.payingcamel.database.fill.FillListAdapter
import java.util.*

class GasFragment : Fragment(R.layout.fragment_gas) {

    private lateinit var gasViewModel: GasViewModel
    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var keyProvider: FillItemKeyProvider
    private val actionModeCallback: ActionModeCallback = ActionModeCallback()
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gasViewModel =
            ViewModelProvider(this).get(GasViewModel::class.java)
        val recyclerView: RecyclerView = view.findViewById(R.id.gas_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = FillListAdapter(context) {}
        recyclerView.adapter = adapter
        keyProvider = FillItemKeyProvider()
        selectionTracker = SelectionTracker.Builder(
            "fillSelection",
            recyclerView,
            keyProvider,
            FillItemDetailsLookup(recyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        adapter.tracker = selectionTracker
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        selectionTracker.addObserver(FillSelectionObserver())

        gasViewModel.allFills.observe(viewLifecycleOwner) { fills ->
            // Update the cached copy of the words in the adapter.
            fills?.let {
                adapter.setFills(it)
                keyProvider.setFills(it)
            }
        }

        val fabGas: FloatingActionButton = view.findViewById(R.id.fab_gas)
        fabGas.setOnClickListener { /*fabView ->*/
            val intent = Intent(activity, NewFillActivity::class.java)
            intent.putExtra(NewFillActivity.EXTRA_CALENDAR, Calendar.getInstance())
            startFillForResult.launch(intent)
        }
    }

    private val startFillForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val fill = Fill(
                        UUID.randomUUID(),
                        data.getIntExtra(NewFillActivity.EXTRA_AMOUNT, 0),
                        data.getSerializableExtra(NewFillActivity.EXTRA_START) as Calendar? ?:Calendar.getInstance(),
                        data.getStringExtra(NewFillActivity.EXTRA_NAME),
                        data.getStringExtra(NewFillActivity.EXTRA_NOTE),
                        getRandomMaterialColor()
                    )
                    gasViewModel.insert(fill)
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

    private fun getRandomMaterialColor(): Int {
        val colors = resources.obtainTypedArray(R.array.mdcolor_400)
        val index = (Math.random() * colors.length()).toInt()
        val returnColor = colors.getColor(index, Color.GRAY)
        colors.recycle()

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
                    val fillList = mutableListOf<Fill>() // CHECK maybe not best way
                    for (uuid in uuidList){
                        gasViewModel.allFills.value?.let{
                            fillList.add(it[keyProvider.getPosition(uuid)])
                        }
                    }

                    gasViewModel.delete(fillList)
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

    private inner class FillSelectionObserver : SelectionTracker.SelectionObserver<String>() {
        override fun onItemStateChanged(key: String, selected: Boolean) {
            super.onItemStateChanged(key, selected)
            enableActionMode()
        }
    }
}