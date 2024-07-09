package com.guillaumetousignant.payingcamel.ui.paths

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
import com.guillaumetousignant.payingcamel.NewPathActivity
import com.guillaumetousignant.payingcamel.database.path.Path
import com.guillaumetousignant.payingcamel.database.path.PathItemDetailsLookup
import com.guillaumetousignant.payingcamel.database.path.PathItemKeyProvider
import com.guillaumetousignant.payingcamel.database.path.PathListAdapter
import java.util.*

class PathsFragment : Fragment(R.layout.fragment_paths) {

    private lateinit var pathsViewModel: PathsViewModel
    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var keyProvider: PathItemKeyProvider
    private val actionModeCallback: ActionModeCallback = ActionModeCallback()
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pathsViewModel =
            ViewModelProvider(this)[PathsViewModel::class.java]
        val recyclerView: RecyclerView = view.findViewById(R.id.paths_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = PathListAdapter(context) {}
        recyclerView.adapter = adapter
        keyProvider = PathItemKeyProvider()
        selectionTracker = SelectionTracker.Builder(
            "pathSelection",
            recyclerView,
            keyProvider,
            PathItemDetailsLookup(recyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        adapter.tracker = selectionTracker
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        selectionTracker.addObserver(PathSelectionObserver())

        pathsViewModel.allPaths.observe(viewLifecycleOwner) { paths ->
            // Update the cached copy of the words in the adapter.
            paths?.let {
                adapter.setPaths(it)
                keyProvider.setPaths(it)
            }
        }

        val fabPaths: FloatingActionButton = view.findViewById(R.id.fab_paths)
        fabPaths.setOnClickListener { /*fabView ->*/
            val intent = Intent(activity, NewPathActivity::class.java)
            startPathForResult.launch(intent)
        }
    }

    private val startPathForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val path = Path(
                        UUID.randomUUID(),
                        data.getDoubleExtra(NewPathActivity.EXTRA_DISTANCE, 0.0),
                        data.getStringExtra(NewPathActivity.EXTRA_FROM),
                        data.getStringExtra(NewPathActivity.EXTRA_TO),
                        data.getStringExtra(NewPathActivity.EXTRA_NAME),
                        data.getStringExtra(NewPathActivity.EXTRA_NOTE),
                        getRandomMaterialColor()
                    )
                    pathsViewModel.insert(path)
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
                    val pathList = mutableListOf<Path>() // CHECK maybe not best way
                    for (uuid in uuidList){
                        pathsViewModel.allPaths.value?.let{
                            pathList.add(it[keyProvider.getPosition(uuid)])
                        }
                    }

                    pathsViewModel.delete(pathList)
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

    private inner class PathSelectionObserver : SelectionTracker.SelectionObserver<String>() {
        override fun onItemStateChanged(key: String, selected: Boolean) {
            super.onItemStateChanged(key, selected)
            enableActionMode()
        }
    }
}