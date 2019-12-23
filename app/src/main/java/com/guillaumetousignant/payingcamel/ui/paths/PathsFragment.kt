package com.guillaumetousignant.payingcamel.ui.paths

import android.app.Activity
import android.content.Intent
import android.graphics.Color
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
import com.guillaumetousignant.payingcamel.NewPathActivity
import com.guillaumetousignant.payingcamel.database.path.Path
import com.guillaumetousignant.payingcamel.database.path.PathListAdapter
import java.util.*

class PathsFragment : Fragment(R.layout.fragment_paths) {

    private val newPathActivityRequestCode = 6
    private lateinit var pathsViewModel: PathsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pathsViewModel =
            ViewModelProviders.of(this).get(PathsViewModel::class.java)
        val recyclerView: RecyclerView = view.findViewById(R.id.paths_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = PathListAdapter {}
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        pathsViewModel.allPaths.observe(this, Observer { paths ->
            // Update the cached copy of the words in the adapter.
            paths?.let { adapter.setPaths(it) }
        })

        val fabPaths: FloatingActionButton = view.findViewById(R.id.fab_paths)
        fabPaths.setOnClickListener { /*fabView ->*/
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            val intent = Intent(activity, NewPathActivity::class.java)
            startActivityForResult(intent, newPathActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newPathActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val path = Path(
                    UUID.randomUUID(),
                    data.getDoubleExtra(NewPathActivity.EXTRA_DISTANCE, 0.0),
                    data.getStringExtra(NewPathActivity.EXTRA_FROM),
                    data.getStringExtra(NewPathActivity.EXTRA_TO),
                    data.getStringExtra(NewPathActivity.EXTRA_NAME),
                    data.getStringExtra(NewPathActivity.EXTRA_NOTE),
                    getRandomMaterialColor(getString(R.string.icon_color_type))
                )
                pathsViewModel.insert(path)
                Unit
            }
        }
        else if (requestCode == newPathActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
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
}