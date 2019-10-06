package com.guillaumetousignant.payingcamel.database

import androidx.room.TypeConverter
import java.util.UUID
import java.util.Calendar

class Converters {
    @TypeConverter
    fun calendarFromTimestamp(value: Long?): Calendar? {// These lose timezone
        //return value?.{ val Calendar.getInstance().setTimeInMillis(it) }

        if (value == null){
            return null
        }
        else {
            val calendar = Calendar.getInstance()
            calendar.setTimeInMillis(value)
            return calendar
        }
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {// These lose timezone
        return calendar?.getTimeInMillis()
    }
} // add for UUID?