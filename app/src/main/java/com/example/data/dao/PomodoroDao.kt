package com.example.data.dao

import androidx.room.*
import com.example.data.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<PomodoroSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSession)

    @Query("DELETE FROM pomodoro_sessions")
    suspend fun deleteAllSessions()
}
