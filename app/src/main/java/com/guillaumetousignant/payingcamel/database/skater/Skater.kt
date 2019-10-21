package com.guillaumetousignant.payingcamel.database.skater

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.UUID
//import java.util.Calendar

@Entity(tableName = "skater_table")
class Skater(@PrimaryKey @ColumnInfo(name = "uuid") val uuid: UUID,
             @ColumnInfo(name = "first_name") val first_name: String,
             @ColumnInfo(name = "last_name") val last_name: String,
             @ColumnInfo(name = "note") val note: String?,
             @ColumnInfo(name = "email") val email: String?,
             @ColumnInfo(name = "active") val active: Boolean)