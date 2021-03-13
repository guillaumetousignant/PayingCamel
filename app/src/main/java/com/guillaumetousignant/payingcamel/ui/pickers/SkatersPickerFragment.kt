package com.guillaumetousignant.payingcamel.ui.pickers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.guillaumetousignant.payingcamel.R
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.database.skater.SkaterItemDetailsLookup
import com.guillaumetousignant.payingcamel.database.skater.SkaterItemKeyProvider
import com.guillaumetousignant.payingcamel.database.skater.SkaterListAdapter

class SkatersPickerFragment(val skaters: MutableLiveData<List<Skater>>, private val allSkaters: LiveData<List<Skater>>) : DialogFragment() {

    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var keyProvider: SkaterItemKeyProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.skaters_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.skaters_picker_dialog_recyclerview)
        val adapter = SkaterListAdapter(context) {
            skaters.postValue(listOf(it)) // CHECK Post list
            dismiss()
        }
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

        allSkaters.observe(this, Observer { skaters ->
            // Update the cached copy of the words in the adapter.
            skaters?.let { adapter.setSkaters(it)
                           keyProvider.setSkaters(it)}
        })

        val fabSkatersPicker: FloatingActionButton = view.findViewById(R.id.fab_skaters_picker)
        fabSkatersPicker.setOnClickListener { /*fabView ->*/
            val uuidList = selectionTracker.selection.toList()
            val skaterList = mutableListOf<Skater>() // CHECK maybe not best way
            for (uuid in uuidList){
                allSkaters.value?.let{ allSkaterList ->
                    skaterList.add(allSkaterList[keyProvider.getPosition(uuid)])
                }
            }

            skaters.postValue(skaterList)
            dismiss()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        selectionTracker.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        selectionTracker.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}