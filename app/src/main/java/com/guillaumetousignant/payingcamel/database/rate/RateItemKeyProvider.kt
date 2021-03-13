package com.guillaumetousignant.payingcamel.database.rate

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class RateItemKeyProvider internal constructor() :
    ItemKeyProvider<String>(SCOPE_CACHED) {

    private var mKeyToPosition: MutableMap<String, Int> = HashMap()
    private var rateList: List<Rate> = emptyList()

    override fun getKey(i: Int): String? {
        return rateList.getOrNull(i)?.uuid?.toString()
    }

    fun getKeys(i: List<Int>): List<String?> {
        val list = mutableListOf<String?>() // CHECK maybe not best way
        for (value in i){
            list.add(rateList[value].uuid.toString())
        }
        return list
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?: RecyclerView.NO_POSITION // CHECK this is sketchy
    }

    fun getPositions(rates: List<String>): List<Int> {
        val list = mutableListOf<Int>() // CHECK maybe not best way
        for (rate in rates){
            list.add(mKeyToPosition[rate]?: RecyclerView.NO_POSITION)
        }
        return list
    }

    fun setRates(newList: List<Rate>){
        rateList = newList
        mKeyToPosition = HashMap(rateList.size) // CHECK maybe just modify?
        for ((index, rate) in rateList.withIndex()) {
            mKeyToPosition[rate.uuid.toString()] = index
        }
    }
}