package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.NoteDao
import com.example.data.dao.PomodoroDao
import com.example.data.dao.TaskDao
import com.example.data.model.Note
import com.example.data.model.PomodoroSession
import com.example.data.model.Task

@Database(entities = [Task::class, Note::class, PomodoroSession::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun pomodoroDao(): PomodoroDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_productivity_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
