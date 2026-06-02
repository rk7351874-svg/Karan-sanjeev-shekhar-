package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.ui.components.GlassCard
import com.example.ui.theme.GlassBorderTint
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleGlow
import com.example.ui.theme.TextMutedGrey
import com.example.viewmodel.ProductivityViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(
    viewModel: ProductivityViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsState()
    val activeNote by viewModel.activeEditingNote.collectAsState()
    val isSaving by viewModel.isSavingNote.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTagFilter by remember { mutableStateOf("All") }

    val tags = listOf("All", "CS204", "MATHS", "PHYSICS", "LIT", "WORK")

    // Filter notes
    val filteredNotes = remember(notes, searchQuery, selectedTagFilter) {
        notes.filter { note ->
            val matchQuery = note.title.contains(searchQuery, ignoreCase = true) || 
                             note.content.contains(searchQuery, ignoreCase = true)
            val matchTag = selectedTagFilter == "All" || note.tag.equals(selectedTagFilter, ignoreCase = true)
            matchQuery && matchTag
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Main view content: standard Note list and filters
        if (activeNote == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Heading UI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KNOWLEDGE BANK",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPurpleGlow,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Class Notes",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White
                        )
                    }

                    // Floating Create item
                    Button(
                        onClick = { viewModel.selectNoteForEditing(Note(title = "", content = "", tag = "CS204")) },
                        modifier = Modifier.testTag("create_note_floating_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPurple,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Note")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar Glass Panel
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search notes...", color = TextMutedGrey) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_notes_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.02f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.02f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tag filtering pill row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        val isSelected = tag == selectedTagFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonPurple else Color.White.copy(alpha = 0.05f))
                                .clickable { selectedTagFilter = tag }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextMutedGrey
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grid View of notes (Google Keep aesthetic)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Prepend default "Create Note" Card action block
                    item {
                        GlassCard(
                            modifier = Modifier
                                .height(120.dp)
                                .clickable { viewModel.selectNoteForEditing(Note(title = "", content = "", tag = "CS204")) }
                                .testTag("quick_create_note_card"),
                            cornerRadius = 20.dp,
                            backgroundColor = Color.Transparent,
                            borderColor = Color.White.copy(alpha = 0.15f),
                            borderWidth = 1.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Note",
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "NEW NOTE",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            onClick = { viewModel.selectNoteForEditing(note) },
                            onDelete = { viewModel.deleteNote(note) }
                        )
                    }
                }
            }
        }

        // Expanded full screen Editor Mode
        activeNote?.let { note ->
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            ) {
                NoteEditorView(
                    note = note,
                    isSaving = isSaving,
                    onBack = { viewModel.selectNoteForEditing(null) },
                    onContentChange = { title, content, tag ->
                        viewModel.updateActiveNoteStateAndAutosave(title, content, tag)
                    },
                    onSaveAndClose = { title, content, tag ->
                        viewModel.manualSaveNote(title, content, tag)
                    }
                )
            }
        }
    }
}

@Composable
fun NoteCardItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .height(130.dp)
            .clickable { onClick() }
            .testTag("note_card_${note.id}"),
        cornerRadius = 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = if (note.title.isBlank()) "Untitled note" else note.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (note.title.isBlank()) Color.White.copy(alpha = 0.3f) else Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(20.dp)
                            .testTag("delete_note_${note.id}"),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (note.content.isBlank()) "Empty content..." else note.content,
                    fontSize = 11.sp,
                    color = TextMutedGrey,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tag footer indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(NeonPurpleGlow)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = note.tag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun NoteEditorView(
    note: Note,
    isSaving: Boolean,
    onBack: () -> Unit,
    onContentChange: (String, String, String) -> Unit,
    onSaveAndClose: (String, String, String) -> Unit
) {
    var titleState by remember(note.id) { mutableStateOf(note.title) }
    var contentState by remember(note.id) { mutableStateOf(note.content) }
    var tagState by remember(note.id) { mutableStateOf(note.tag) }

    val quickTags = listOf("CS204", "MATHS", "PHYSICS", "LIT", "WORK")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Safe inset spacer/navigation title row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("close_editor_button")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSaving) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Saving",
                        tint = MintGreen,
                        modifier = Modifier
                            .size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto-saving...", fontSize = 11.sp, color = MintGreen, fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Saved",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto-saved", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                }
            }

            // Save and Back active trigger button
            IconButton(
                onClick = { onSaveAndClose(titleState, contentState, tagState) },
                modifier = Modifier.testTag("save_note_button")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Confirm", tint = NeonPurpleGlow)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Topic select Tag deck
        Text("SUBJECT TAG", style = MaterialTheme.typography.labelSmall, color = TextMutedGrey)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickTags.forEach { currentTag ->
                val isActiveTag = tagState == currentTag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActiveTag) NeonPurple else Color.White.copy(alpha = 0.05f))
                        .clickable {
                            tagState = currentTag
                            onContentChange(titleState, contentState, currentTag)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = currentTag,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isActiveTag) Color.White else TextMutedGrey
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rich Header Textfield
        BasicTextField(
            value = titleState,
            onValueChange = {
                titleState = it
                onContentChange(it, contentState, tagState)
            },
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            cursorBrush = SolidColor(NeonPurpleGlow),
            decorationBox = { innerTextField ->
                if (titleState.isEmpty()) {
                    Text("Course Title...", style = MaterialTheme.typography.headlineMedium, color = Color.White.copy(alpha = 0.2f))
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("note_title_field")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Rich tools toolbar formatter deck
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            cornerRadius = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconButton(
                    onClick = {
                        val caret = "### "
                        contentState += "\n$caret"
                        onContentChange(titleState, contentState, tagState)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("H3", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }

                IconButton(
                    onClick = {
                        val caret = "**bold text**"
                        contentState += caret
                        onContentChange(titleState, contentState, tagState)
                    },
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.FormatBold, contentDescription = "Add bold Heading")
                }

                IconButton(
                    onClick = {
                        val caret = "\n• "
                        contentState += caret
                        onContentChange(titleState, contentState, tagState)
                    },
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.FormatListBulleted, contentDescription = "Add bullet point")
                }

                IconButton(
                    onClick = {
                        val caret = "\n[ ] Checkbox item"
                        contentState += caret
                        onContentChange(titleState, contentState, tagState)
                    },
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Add Checkbox")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Rich Content Area Textfield
        BasicTextField(
            value = contentState,
            onValueChange = {
                contentState = it
                onContentChange(titleState, it, tagState)
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = FontFamily.Default,
                fontSize = 16.sp
            ),
            cursorBrush = SolidColor(NeonPurple),
            decorationBox = { innerTextField ->
                if (contentState.isEmpty()) {
                    Text(
                        "Start typing your study notes. Supports standard formatted templates...\n\nUse ### for bold section headings.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.2f)
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .heightIn(min = 300.dp)
                .testTag("note_content_field")
        )
    }
}
