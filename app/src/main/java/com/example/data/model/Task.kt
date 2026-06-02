package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Exam", "Assignment", "Revision", "Personal"
    val isCompleted: Boolean = false,
    val dueTime: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
