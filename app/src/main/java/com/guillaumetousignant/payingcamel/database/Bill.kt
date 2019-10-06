package com.guillaumetousignant.payingcamel.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.UUID
import java.util.Calendar

@Entity(tableName = "bill_table")
class Bill(@PrimaryKey @ColumnInfo(name = "uuid") val uuid: UUID,
           @ColumnInfo(name = "skater") val skater: UUID,
           @ColumnInfo(name = "start_time") val start_time: Calendar,
           @ColumnInfo(name = "end_time") val end_time: Calendar,
           @ColumnInfo(name = "rate") val rate: Int,
           @ColumnInfo(name = "amount") val amount: Int,
           @ColumnInfo(name = "note") val note: String?,
           @ColumnInfo(name = "paid") val paid: Boolean)