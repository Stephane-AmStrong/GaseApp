package com.amstrong.gaseapp.data.db.entities

import androidx.room.TypeConverter
import java.util.*

object DateConverter {
//    @TypeConverter
//    fun toDate(dateLong: Long?): Date? {
//        return dateLong?.let { Date(it) }
//    }
//
//    @TypeConverter
//    fun fromDate(date: Date?): Long? {
//        return if (date == null) null else date.getTime()
//    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }


}

