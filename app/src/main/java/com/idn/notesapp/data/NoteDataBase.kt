package com.idn.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.idn.notesapp.data.model.NoteData

@Database(entities = [NoteData::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class NoteDataBase : RoomDatabase() {
    abstract fun noteDao() : NoteDao

    companion object {
        @Volatile
        private var INSTANCE : NoteDataBase? = null

        fun getDataBase(context: Context) : NoteDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDataBase::class.java,
                    "note_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}