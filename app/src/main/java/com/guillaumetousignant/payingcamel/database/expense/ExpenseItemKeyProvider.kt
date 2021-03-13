package com.guillaumetousignant.payingcamel.database.expense

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class ExpenseItemKeyProvider internal constructor() :
    ItemKeyProvider<String>(SCOPE_CACHED) {

    private var mKeyToPosition: MutableMap<String, Int> = HashMap()
    private var expenseList: List<Expense> = emptyList()

    override fun getKey(i: Int): String? {
        return expenseList.getOrNull(i)?.uuid.toString()
    }

    fun getKeys(i: List<Int>): List<String?> {
        val list = mutableListOf<String?>() // CHECK maybe not best way
        for (value in i){
            list.add(expenseList[value].uuid.toString())
        }
        return list
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?: RecyclerView.NO_POSITION // CHECK this is sketchy
    }

    fun getPositions(expenses: List<String>): List<Int> {
        val list = mutableListOf<Int>() // CHECK maybe not best way
        for (expense in expenses){
            list.add(mKeyToPosition[expense]?: RecyclerView.NO_POSITION)
        }
        return list
    }

    fun setExpenses(newList: List<Expense>){
        expenseList = newList
        mKeyToPosition = HashMap(expenseList.size) // CHECK maybe just modify?
        for ((index, expense) in expenseList.withIndex()) {
            mKeyToPosition[expense.uuid.toString()] = index
        }
    }
}