package com.guillaumetousignant.payingcamel.ui.skaters

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
import com.guillaumetousignant.payingcamel.NewSkaterActivity
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.database.skater.SkaterItemDetailsLookup
import com.guillaumetousignant.payingcamel.database.skater.SkaterItemKeyProvider
import com.guillaumetousignant.payingcamel.database.skater.SkaterListAdapter
import java.util.*

class SkatersFragment : Fragment(R.layout.fragment_skaters) {

    private lateinit var skatersViewModel: SkatersViewModel
    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var keyProvider: SkaterItemKeyProvider
    private val actionModeCallback: ActionModeCallback = ActionModeCallback()
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skatersViewModel =
            ViewModelProvider(this).get(SkatersViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.skaters_recyclerview)
        val adapter = SkaterListAdapter(context) {}
        recyclerView.adapter = adapter
        keyProvider = SkaterItemKeyProvider()
        selectionTracker = SelectionTracker.Builder(
            "skaterSelection",
            recyclerView,
            keyProvider,
            SkaterItemDetailsLookup(recyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        adapter.tracker = selectionTracker
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        selectionTracker.addObserver(SkaterSelectionObserver())

        skatersViewModel.allSkaters.observe(viewLifecycleOwner, { skaters ->
            // Update the cached copy of the words in the adapter.
            skaters?.let { adapter.setSkaters(it)
                keyProvider.setSkaters(it)}
        })

        val fabSkaters: FloatingActionButton = view.findViewById(R.id.fab_skaters)
        fabSkaters.setOnClickListener { /*fabView ->*/
            val intent = Intent(activity, NewSkaterActivity::class.java)
            startSkaterForResult.launch(intent)
        }
    }

    private val startSkaterForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val skater = Skater(
                        UUID.randomUUID(),
                        data.getStringExtra(NewSkaterActivity.EXTRA_FIRST_NAME) ?: "",
                        data.getStringExtra(NewSkaterActivity.EXTRA_LAST_NAME) ?: "",
                        data.getStringExtra(NewSkaterActivity.EXTRA_NOTE),
                        data.getStringExtra(NewSkaterActivity.EXTRA_EMAIL),
                        data.getBooleanExtra(NewSkaterActivity.EXTRA_ACTIVE, true),
                        getRandomMaterialColor(getString(R.string.icon_color_type))
                    )
                    skatersViewModel.insert(skater)
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
                    val skaterList = mutableListOf<Skater>() // CHECK maybe not best way
                    for (uuid in uuidList){
                        skatersViewModel.allSkaters.value?.let{
                            skaterList.add(it[keyProvider.getPosition(uuid)])
                        }
                    }

                    skatersViewModel.delete(skaterList)
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

    private inner class SkaterSelectionObserver : SelectionTracker.SelectionObserver<String>() {
        override fun onItemStateChanged(key: String, selected: Boolean) {
            super.onItemStateChanged(key, selected)
            enableActionMode()
        }
    }
}