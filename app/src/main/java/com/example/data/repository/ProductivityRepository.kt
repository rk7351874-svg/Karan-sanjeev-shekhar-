package com.example.data.repository

import com.example.data.dao.NoteDao
import com.example.data.dao.PomodoroDao
import com.example.data.dao.TaskDao
import com.example.data.model.Note
import com.example.data.model.PomodoroSession
import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

class ProductivityRepository(
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val pomodoroDao: PomodoroDao
) {
    // Tasks flow & operations
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    suspend fun deleteTaskById(id: Int) = taskDao.deleteTaskById(id)

    // Notes flow & operations
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    
    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)
    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    suspend fun deleteNoteById(id: Int) = noteDao.deleteNoteById(id)

    // Pomodoro flow & operations
    val allSessions: Flow<List<PomodoroSession>> = pomodoroDao.getAllSessions()
    
    suspend fun insertSession(session: PomodoroSession) = pomodoroDao.insertSession(session)
    suspend fun deleteAllSessions() = pomodoroDao.deleteAllSessions()
}
