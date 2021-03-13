package com.guillaumetousignant.payingcamel.database.skater

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class SkaterItemKeyProvider internal constructor() :
    ItemKeyProvider<String>(SCOPE_CACHED) {

    private var mKeyToPosition: MutableMap<String, Int> = HashMap()
    private var skaterList: List<Skater> = emptyList()

    override fun getKey(i: Int): String? {
        return skaterList.getOrNull(i)?.uuid.toString()
    }

    fun getKeys(i: List<Int>): List<String?> {
        val list = mutableListOf<String?>() // CHECK maybe not best way
        for (value in i){
            list.add(skaterList[value].uuid.toString())
        }
        return list
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?: RecyclerView.NO_POSITION // CHECK this is sketchy
    }

    fun getPositions(skaters: List<String>): List<Int> {
        val list = mutableListOf<Int>() // CHECK maybe not best way
        for (skater in skaters){
            list.add(mKeyToPosition[skater]?: RecyclerView.NO_POSITION)
        }
        return list
    }

    fun setSkaters(newList: List<Skater>){
        skaterList = newList
        mKeyToPosition = HashMap(skaterList.size) // CHECK maybe just modify?
        for ((index, skater) in skaterList.withIndex()) {
            mKeyToPosition[skater.uuid.toString()] = index
        }
    }
}