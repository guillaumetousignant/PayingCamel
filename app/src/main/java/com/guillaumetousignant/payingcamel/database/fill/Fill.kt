package com.guillaumetousignant.payingcamel.database.fill

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.UUID
import android.icu.util.Calendar

@Entity(tableName = "fill_table")
class Fill(@PrimaryKey @ColumnInfo(name = "uuid") val uuid: UUID,
           @ColumnInfo(name = "amount") val amount: Int,
           @ColumnInfo(name = "start_time") val start_time: Calendar,
           @ColumnInfo(name = "name") val name: String?,
           @ColumnInfo(name = "note") val note: String?)