package com.guillaumetousignant.payingcamel.database.expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.UUID
import android.icu.util.Calendar

@Entity(tableName = "expense_table")
class Expense(@PrimaryKey @ColumnInfo(name = "uuid") val uuid: UUID,
              @ColumnInfo(name = "amount") val amount: Int,
              @ColumnInfo(name = "start_time") val start_time: Calendar,
              @ColumnInfo(name = "course") val course: UUID?,
              @ColumnInfo(name = "skater") val skater: UUID?,
              @ColumnInfo(name = "name") val name: String?,
              @ColumnInfo(name = "note") val note: String?,
              @ColumnInfo(name = "color") val color: Int)