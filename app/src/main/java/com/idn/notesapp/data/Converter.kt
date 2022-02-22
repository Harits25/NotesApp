package com.idn.notesapp.data

import androidx.room.TypeConverter
import com.idn.notesapp.data.model.Priority

class Converter {

    @TypeConverter
    fun fromPriority(priorytiy: Priority) : String {
        return priorytiy.name

    }

    @TypeConverter
    fun toPriority(priorytiy: String) : Priority {
        return Priority.valueOf(priorytiy)

    }
}