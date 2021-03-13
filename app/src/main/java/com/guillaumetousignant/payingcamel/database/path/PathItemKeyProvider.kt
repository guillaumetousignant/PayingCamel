package com.guillaumetousignant.payingcamel.database.path

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class PathItemKeyProvider internal constructor() :
    ItemKeyProvider<String>(SCOPE_CACHED) {

    private var mKeyToPosition: MutableMap<String, Int> = HashMap()
    private var pathList: List<Path> = emptyList()

    override fun getKey(i: Int): String? {
        return pathList.getOrNull(i)?.toString()
    }

    fun getKeys(i: List<Int>): List<String?> {
        val list = mutableListOf<String?>() // CHECK maybe not best way
        for (value in i){
            list.add(pathList[value].uuid.toString())
        }
        return list
    }

    override fun getPosition(s: String): Int {
        return mKeyToPosition[s]?: RecyclerView.NO_POSITION // CHECK this is sketchy
    }

    fun getPositions(paths: List<String>): List<Int> {
        val list = mutableListOf<Int>() // CHECK maybe not best way
        for (path in paths){
            list.add(mKeyToPosition[path]?: RecyclerView.NO_POSITION)
        }
        return list
    }

    fun setPaths(newList: List<Path>){
        pathList = newList
        mKeyToPosition = HashMap(pathList.size) // CHECK maybe just modify?
        for ((index, path) in pathList.withIndex()) {
            mKeyToPosition[path.uuid.toString()] = index
        }
    }
}