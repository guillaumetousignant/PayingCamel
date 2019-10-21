package com.guillaumetousignant.payingcamel.ui.paths

import android.app.Activity
import android.content.Intent
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
import com.guillaumetousignant.payingcamel.NewPathActivity
import com.guillaumetousignant.payingcamel.database.path.Path
import com.guillaumetousignant.payingcamel.database.path.PathListAdapter
import java.util.*

class PathsFragment : Fragment() {

    private val newPathActivityRequestCode = 6
    private lateinit var pathsViewModel: PathsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pathsViewModel =
            ViewModelProviders.of(this).get(PathsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_paths, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.paths_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = PathListAdapter {}
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        pathsViewModel.allPaths.observe(this, Observer { paths ->
            // Update the cached copy of the words in the adapter.
            paths?.let { adapter.setPaths(it) }
        })

        val fabPaths: FloatingActionButton = root.findViewById(R.id.fab_paths)
        fabPaths.setOnClickListener { view ->
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            val intent = Intent(activity, NewPathActivity::class.java)
            startActivityForResult(intent, newPathActivityRequestCode)
        }

        return root
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
                    data.getStringExtra(NewPathActivity.EXTRA_NOTE)
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