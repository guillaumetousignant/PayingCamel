package com.guillaumetousignant.payingcamel.database.fill

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class FillItemKeyProvider internal constructor() :
    ItemKeyProvider<String>(SCOPE_CACHED) {

    private var mKeyToPosition: MutableMap<String, Int> = HashMap()
    private var fillList: List<Fill> = emptyList()

    override fun getKey(i: Int): String? {
        return fillList[i].uuid.toString()
    }

    fun getKeys(i: List<Int>): List<String?> {
        val list = mutableListOf<String?>() // CHECK maybe not best way
        for (value in i){
            list.add(fillList[value].uuid.toString())
        }
        return list
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?: RecyclerView.NO_POSITION // CHECK this is sketchy
    }

    fun getPositions(fills: List<String>): List<Int> {
        val list = mutableListOf<Int>() // CHECK maybe not best way
        for (fill in fills){
            list.add(mKeyToPosition[fill]?: RecyclerView.NO_POSITION)
        }
        return list
    }

    fun setFills(newList: List<Fill>){
        fillList = newList
        mKeyToPosition = HashMap(fillList.size) // CHECK maybe just modify?
        for ((index, fill) in fillList.withIndex()) {
            mKeyToPosition[fill.uuid.toString()] = index
        }
    }
}