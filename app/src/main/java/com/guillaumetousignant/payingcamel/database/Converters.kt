package com.guillaumetousignant.payingcamel.database

import androidx.room.TypeConverter
import java.util.UUID
import android.icu.util.Calendar

class Converters {
    @TypeConverter
    fun calendarFromTimestamp(value: Long?): Calendar? {// These lose timezone
        return value?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar
        }
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {// These lose timezone
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun uuidFromString(value: String?): UUID? {
        return if (value == null){
            null
        }
        else {
            UUID.fromString(value)
        }
        //return value?.{ val UUID.fromString(it) } // why doesn't this work
    }

    @TypeConverter
    fun uuidToString(uuid: UUID?): String? {
        return uuid?.toString()
    }
}