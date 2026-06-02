package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val tag: String = "", // e.g. "CS204" or subject
    val lastUpdated: Long = System.currentTimeMillis()
)
