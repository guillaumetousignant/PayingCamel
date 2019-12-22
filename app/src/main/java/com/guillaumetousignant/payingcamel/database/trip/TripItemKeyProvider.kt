package com.guillaumetousignant.payingcamel.database.trip

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class TripItemKeyProvider internal constructor() :
    ItemKeyProvider<String>(SCOPE_CACHED) {

    private var mKeyToPosition: MutableMap<String, Int> = HashMap()
    private var tripList: List<Trip> = emptyList()

    override fun getKey(i: Int): String? {
        return tripList[i].uuid.toString()
    }

    fun getKeys(i: List<Int>): List<String?> {
        val list = mutableListOf<String?>() // CHECK maybe not best way
        for (value in i){
            list.add(tripList[value].uuid.toString())
        }
        return list
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?: RecyclerView.NO_POSITION // CHECK this is sketchy
    }

    fun getPositions(trips: List<String>): List<Int> {
        val list = mutableListOf<Int>() // CHECK maybe not best way
        for (trip in trips){
            list.add(mKeyToPosition[trip]?: RecyclerView.NO_POSITION)
        }
        return list
    }

    fun setTrips(newList: List<Trip>){
        tripList = newList
        mKeyToPosition = HashMap(tripList.size) // CHECK maybe just modify?
        for ((index, trip) in tripList.withIndex()) {
            mKeyToPosition[trip.uuid.toString()] = index
        }
    }
}