package com.guillaumetousignant.payingcamel.ui.expenses

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
import com.guillaumetousignant.payingcamel.NewExpenseActivity
import com.guillaumetousignant.payingcamel.database.expense.Expense
import com.guillaumetousignant.payingcamel.database.expense.ExpenseListAdapter
import java.util.*

class ExpensesFragment : Fragment(R.layout.fragment_expenses) {

    private val newExpenseActivityRequestCode = 4
    private lateinit var expensesViewModel: ExpensesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expensesViewModel =
            ViewModelProviders.of(this).get(ExpensesViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.expenses_recyclerview)
        //val adapter = CourseListAdapter(this)
        val adapter = ExpenseListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) // CHECK can return null

        expensesViewModel.allExpenses.observe(this, Observer { expenses ->
            // Update the cached copy of the words in the adapter.
            expenses?.let { adapter.setExpenses(it) }
        })

        val fabExpenses: FloatingActionButton = view.findViewById(R.id.fab_expenses)
        fabExpenses.setOnClickListener { /*fabView ->*/
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/

            val intent = Intent(activity, NewExpenseActivity::class.java)
            intent.putExtra(NewExpenseActivity.EXTRA_CALENDAR, Calendar.getInstance())
            startActivityForResult(intent, newExpenseActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newExpenseActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val expense = Expense(
                    UUID.randomUUID(),
                    data.getIntExtra(NewExpenseActivity.EXTRA_AMOUNT, 0),
                    data.getSerializableExtra(NewExpenseActivity.EXTRA_START) as Calendar,
                    data.getSerializableExtra(NewExpenseActivity.EXTRA_COURSE) as UUID?,
                    data.getSerializableExtra(NewExpenseActivity.EXTRA_SKATER) as UUID?,
                    data.getStringExtra(NewExpenseActivity.EXTRA_NAME),
                    data.getStringExtra(NewExpenseActivity.EXTRA_NOTE)
                )
                expensesViewModel.insert(expense)
                Unit
            }
        }
        else if (requestCode == newExpenseActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
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