package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.GlassCard
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleGlow
import com.example.ui.theme.TextMutedGrey
import com.example.viewmodel.ProductivityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    viewModel: ProductivityViewModel,
    modifier: Modifier = Modifier
) {
    val focusMin by viewModel.focusDurationMinutes.collectAsState()
    val breakMin by viewModel.breakDurationMinutes.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val avatarSeed by viewModel.userAvatarSeed.collectAsState()

    val sessions by viewModel.sessions.collectAsState()
    val totalFocusMin by viewModel.totalFocusMinutes.collectAsState(initial = 0)
    val totalTasksCompleted by viewModel.totalCompletedTasks.collectAsState(initial = 0)

    var inputName by remember { mutableStateOf(userName) }
    var inputSeed by remember { mutableStateOf(avatarSeed) }

    var localFocusVal by remember(focusMin) { mutableFloatStateOf(focusMin.toFloat()) }
    var localBreakVal by remember(breakMin) { mutableFloatStateOf(breakMin.toFloat()) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Screen Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SETTINGS & STATISTICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPurpleGlow,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Control Center",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Profile customizer card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_settings_panel"),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "STUDENT PROFILE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://api.dicebear.com/7.x/avataaars/svg?seed=$inputSeed",
                        contentDescription = "Avatar preview",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.5.dp, NeonPurpleGlow, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Edit Username field
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            label = { Text("Display Name", fontSize = 11.sp) },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Edit Avatar seed field
                        OutlinedTextField(
                            value = inputSeed,
                            onValueChange = { inputSeed = it },
                            label = { Text("Avatar Seed", fontSize = 11.sp) },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonPurple,
                                 unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        viewModel.userName.value = inputName.ifBlank { "Alex Rivers" }
                        viewModel.userAvatarSeed.value = inputSeed.ifBlank { "Alex" }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_profile_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save Profile", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Changes", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Pomodoro Length parameters adjusts
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pomodoro_settings_panel"),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "TIMER INTERVALS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Study length slider block
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Study Block Duration", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Text("${localFocusVal.toInt()} min", style = MaterialTheme.typography.bodyMedium, color = NeonPurpleGlow, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = localFocusVal,
                        onValueChange = { localFocusVal = it },
                        onValueChangeFinished = { viewModel.updateSettings(localFocusVal.toInt(), localBreakVal.toInt()) },
                        valueRange = 5f..60f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPurple,
                            activeTrackColor = NeonPurpleGlow,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.testTag("study_slider")
                    )
                }

                // Break length slider block
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Break Block Duration", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Text("${localBreakVal.toInt()} min", style = MaterialTheme.typography.bodyMedium, color = MintGreen, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = localBreakVal,
                        onValueChange = { localBreakVal = it },
                        onValueChangeFinished = { viewModel.updateSettings(localFocusVal.toInt(), localBreakVal.toInt()) },
                        valueRange = 1f..30f,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = MintGreen,
                            activeTrackColor = MintGreen,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.testTag("break_slider")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Total Academic Statistics dashboard row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = "Focus Time", tint = NeonPurpleGlow, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalFocusMin m", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Total Focus Minutes", fontSize = 10.sp, color = TextMutedGrey)
                }
            }

            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.AssignmentTurnedIn, contentDescription = "Completed tasks", tint = MintGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalTasksCompleted", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Completed Tasks", fontSize = 10.sp, color = TextMutedGrey)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Database session logs list
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STUDY LOGS HISTORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (sessions.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.deleteAllSessions() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Restore, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (sessions.isEmpty()) {
                    Text(
                        text = "No focus interval logs recorded yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedGrey,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sessions.take(6).forEach { session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.02f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (session.type == "Focus Session") NeonPurpleGlow else MintGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${session.durationMinutes}m ${session.type}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = dateFormat.format(Date(session.timestamp)),
                                    fontSize = 10.sp,
                                    color = TextMutedGrey
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
