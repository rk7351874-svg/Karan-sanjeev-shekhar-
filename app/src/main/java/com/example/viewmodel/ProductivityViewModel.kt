package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Note
import com.example.data.model.PomodoroSession
import com.example.data.model.Task
import com.example.data.repository.ProductivityRepository
import com.example.util.AmbientAudioPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ProductivityViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ProductivityRepository(
        taskDao = database.taskDao(),
        noteDao = database.noteDao(),
        pomodoroDao = database.pomodoroDao()
    )

    // Screen State
    enum class Screen { FOCUS, TASKS, NOTES, SETUP }
    private val _currentScreen = MutableStateFlow(Screen.FOCUS)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // User Settings Profile State
    val userName = MutableStateFlow("Alex Rivers")
    val userAvatarSeed = MutableStateFlow("Alex")

    // Pomodoro Timer State
    private val _focusDurationMinutes = MutableStateFlow(25)
    val focusDurationMinutes: StateFlow<Int> = _focusDurationMinutes.asStateFlow()

    private val _breakDurationMinutes = MutableStateFlow(5)
    val breakDurationMinutes: StateFlow<Int> = _breakDurationMinutes.asStateFlow()

    private val _currentSessionType = MutableStateFlow("Focus Session") // "Focus Session" or "Short Break"
    val currentSessionType: StateFlow<String> = _currentSessionType.asStateFlow()

    private val _timerTimeLeft = MutableStateFlow(25 * 60) // 25 minutes default in seconds
    val timerTimeLeft: StateFlow<Int> = _timerTimeLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // Ambient Sound State
    val ambientSounds = listOf("Lo-Fi Beats", "Rain Drops", "White Noise", "None")
    private val _selectedAmbientSound = MutableStateFlow("Lo-Fi Beats")
    val selectedAmbientSound: StateFlow<String> = _selectedAmbientSound.asStateFlow()

    private val _isAmbientPlaying = MutableStateFlow(false)
    val isAmbientPlaying: StateFlow<Boolean> = _isAmbientPlaying.asStateFlow()

    private val ambientAudioPlayer = AmbientAudioPlayer()

    // Database flows (Reactive UI updates)
    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<PomodoroSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Editing Note State
    private val _activeEditingNote = MutableStateFlow<Note?>(null)
    val activeEditingNote: StateFlow<Note?> = _activeEditingNote.asStateFlow()

    private val _isSavingNote = MutableStateFlow(false)
    val isSavingNote: StateFlow<Boolean> = _isSavingNote.asStateFlow()

    private var autoSaveJob: Job? = null

    init {
        // Automatically sync timer limits with configured times
        resetTimer()
    }

    // Timer Actions
    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        // If sound is enabled and was playing, restart it
        if (_selectedAmbientSound.value != "None" && _isAmbientPlaying.value) {
            ambientAudioPlayer.startPlaying(_selectedAmbientSound.value)
        }

        timerJob = viewModelScope.launch {
            while (isActive && _timerTimeLeft.value > 0) {
                delay(1000)
                _timerTimeLeft.value -= 1
            }
            if (_timerTimeLeft.value == 0) {
                onTimerFinish()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
        ambientAudioPlayer.stopPlaying()
    }

    fun resetTimer() {
        pauseTimer()
        val minutes = if (_currentSessionType.value == "Focus Session") {
            _focusDurationMinutes.value
        } else {
            _breakDurationMinutes.value
        }
        _timerTimeLeft.value = minutes * 60
    }

    fun updateSettings(focusMin: Int, breakMin: Int) {
        _focusDurationMinutes.value = focusMin.coerceIn(1, 120)
        _breakDurationMinutes.value = breakMin.coerceIn(1, 60)
        resetTimer()
    }

    private fun onTimerFinish() {
        pauseTimer()
        viewModelScope.launch {
            if (_currentSessionType.value == "Focus Session") {
                // Log completed focus session to DB
                repository.insertSession(
                    PomodoroSession(
                        durationMinutes = _focusDurationMinutes.value,
                        type = "Focus Session"
                    )
                )
                // Switch to Break
                _currentSessionType.value = "Short Break"
                _timerTimeLeft.value = _breakDurationMinutes.value * 60
            } else {
                // Log break completion optional
                repository.insertSession(
                    PomodoroSession(
                        durationMinutes = _breakDurationMinutes.value,
                        type = "Short Break"
                    )
                )
                // Switch to Focus
                _currentSessionType.value = "Focus Session"
                _timerTimeLeft.value = _focusDurationMinutes.value * 60
            }
            // Trigger sound indicator of session block complete if desired
            // Auto start next session is off by default
        }
    }

    // Ambient Sound Actions
    fun selectAmbientSound(sound: String) {
        _selectedAmbientSound.value = sound
        if (_isAmbientPlaying.value && sound != "None") {
            if (_isTimerRunning.value) {
                ambientAudioPlayer.startPlaying(sound)
            }
        } else {
            ambientAudioPlayer.stopPlaying()
        }
    }

    fun toggleAmbientPlaying() {
        if (_selectedAmbientSound.value == "None") {
            _isAmbientPlaying.value = false
            return
        }
        _isAmbientPlaying.value = !_isAmbientPlaying.value
        if (_isAmbientPlaying.value) {
            ambientAudioPlayer.startPlaying(_selectedAmbientSound.value)
        } else {
            ambientAudioPlayer.stopPlaying()
        }
    }

    // Task Actions
    fun addTask(title: String, category: String, dueTime: String? = null) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    title = title.trim(),
                    category = category,
                    dueTime = dueTime
                )
            )
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Note Actions
    fun selectNoteForEditing(note: Note?) {
        _activeEditingNote.value = note
    }

    fun updateActiveNoteStateAndAutosave(title: String, content: String, tag: String) {
        val current = _activeEditingNote.value
        val updated = current?.copy(
            title = title,
            content = content,
            tag = tag,
            lastUpdated = System.currentTimeMillis()
        ) ?: Note(
            title = title,
            content = content,
            tag = tag,
            lastUpdated = System.currentTimeMillis()
        )
        
        _activeEditingNote.value = updated

        // Set up the debounced saving task
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            _isSavingNote.value = true
            delay(1000) // 1 second debounce
            if (updated.id == 0) {
                val newId = repository.insertNote(updated)
                _activeEditingNote.value = updated.copy(id = newId.toInt())
            } else {
                repository.updateNote(updated)
            }
            _isSavingNote.value = false
        }
    }

    fun manualSaveNote(title: String, content: String, tag: String) {
        autoSaveJob?.cancel()
        val current = _activeEditingNote.value
        val noteToSave = current?.copy(
            title = title,
            content = content,
            tag = tag,
            lastUpdated = System.currentTimeMillis()
        ) ?: Note(
            title = title,
            content = content,
            tag = tag,
            lastUpdated = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _isSavingNote.value = true
            if (noteToSave.id == 0) {
                val newId = repository.insertNote(noteToSave)
                _activeEditingNote.value = noteToSave.copy(id = newId.toInt())
            } else {
                repository.updateNote(noteToSave)
            }
            _isSavingNote.value = false
            _activeEditingNote.value = null // Close editor after save
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
            if (_activeEditingNote.value?.id == note.id) {
                _activeEditingNote.value = null
            }
        }
    }

    fun deleteAllSessions() {
        viewModelScope.launch {
            repository.deleteAllSessions()
        }
    }

    // Stats calculations
    val totalFocusMinutes: Flow<Int> = sessions.map { list ->
        list.filter { it.type == "Focus Session" }.sumOf { it.durationMinutes }
    }

    val totalCompletedTasks: Flow<Int> = tasks.map { list ->
        list.count { it.isCompleted }
    }

    override fun onCleared() {
        super.onCleared()
        ambientAudioPlayer.release()
    }
}
