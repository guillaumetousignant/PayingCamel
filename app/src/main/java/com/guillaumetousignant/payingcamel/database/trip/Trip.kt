package com.guillaumetousignant.payingcamel.database.trip

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.UUID
import android.icu.util.Calendar

@Entity(tableName = "trip_table")
class Trip(@PrimaryKey @ColumnInfo(name = "uuid") val uuid: UUID,
           @ColumnInfo(name = "path") val path: String?,
           @ColumnInfo(name = "from") val from: String?,
           @ColumnInfo(name = "to") val to: String?,
           @ColumnInfo(name = "distance") val distance: Double,
           @ColumnInfo(name = "start_time") val start_time: Calendar,
           @ColumnInfo(name = "course") val course: UUID?,
           @ColumnInfo(name = "skater") val skater: UUID?,
           @ColumnInfo(name = "name") val name: String?,
           @ColumnInfo(name = "note") val note: String?,
           @ColumnInfo(name = "color") val color: Int)