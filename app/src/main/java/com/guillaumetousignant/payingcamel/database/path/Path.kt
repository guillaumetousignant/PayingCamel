package com.guillaumetousignant.payingcamel.database.path

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.UUID

@Entity(tableName = "path_table")
class Path(@PrimaryKey @ColumnInfo(name = "uuid") val uuid: UUID,
           @ColumnInfo(name = "distance") val distance: Double,
           @ColumnInfo(name = "from") val from: String?,
           @ColumnInfo(name = "to") val to: String?,
           @ColumnInfo(name = "name") val name: String?,
           @ColumnInfo(name = "note") val note: String?,
           @ColumnInfo(name = "color") val color: Int)