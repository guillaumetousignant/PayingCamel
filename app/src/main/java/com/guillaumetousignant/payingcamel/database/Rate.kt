package com.guillaumetousignant.payingcamel.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.UUID

@Entity(tableName = "rate_table")
class Rate(@PrimaryKey @ColumnInfo(name = "uuid") val uuid: UUID,
           @ColumnInfo(name = "amount") val amount: Int,
           @ColumnInfo(name = "name") val name: String?,
           @ColumnInfo(name = "note") val note: String?,
           @ColumnInfo(name = "skater") val skater: UUID?)