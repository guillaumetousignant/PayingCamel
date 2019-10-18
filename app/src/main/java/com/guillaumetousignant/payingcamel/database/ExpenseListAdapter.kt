package com.guillaumetousignant.payingcamel.database

//import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guillaumetousignant.payingcamel.R

//import androidx.fragment.app.Fragment


class ExpenseListAdapter internal constructor(
    //context: Context
    //inflater_in: LayoutInflater
) : RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>() {

    //private val inflater: LayoutInflater = LayoutInflater.from(context)
    //private val inflater = inflater_in
    private var expenses = emptyList<Expense>() // Cached copy of words

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expenseItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        //val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return ExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val current = expenses[position]
        holder.expenseItemView.text = current.name?:"Default expense name"
    }

    internal fun setExpenses(expenses: List<Expense>) {
        this.expenses = expenses
        notifyDataSetChanged()
    }

    override fun getItemCount() = expenses.size
}