package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.GlassCard
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleGlow
import com.example.ui.theme.TextMutedGrey
import com.example.viewmodel.ProductivityViewModel
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: ProductivityViewModel,
    modifier: Modifier = Modifier
) {
    val timeLeft by viewModel.timerTimeLeft.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val sessionType by viewModel.currentSessionType.collectAsState()
    val selectedSound by viewModel.selectedAmbientSound.collectAsState()
    val isAmbientPlaying by viewModel.isAmbientPlaying.collectAsState()

    val focusMinutes by viewModel.focusDurationMinutes.collectAsState()
    val breakMinutes by viewModel.breakDurationMinutes.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val avatarSeed by viewModel.userAvatarSeed.collectAsState()

    val totalDurationSeconds = if (sessionType == "Focus Session") {
        focusMinutes * 60
    } else {
        breakMinutes * 60
    }

    val progressFraction = if (totalDurationSeconds > 0) {
        timeLeft.toFloat() / totalDurationSeconds.toFloat()
    } else {
        1.0f
    }

    val minutesDisplay = timeLeft / 60
    val secondsDisplay = timeLeft % 60
    val formattedTime = String.format("%02d:%02d", minutesDisplay, secondsDisplay)

    // Animation for equalizer and neon glowing aura
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Profile Welcome Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "STAY FOCUSED",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPurpleGlow,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White
                )
            }
            // Generate visual avatar seed securely
            AsyncImage(
                model = "https://api.dicebear.com/7.x/avataaars/svg?seed=$avatarSeed",
                contentDescription = "Student Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.navigateTo(ProductivityViewModel.Screen.SETUP) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Circular Countdown Timer Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 8.dp),
            cornerRadius = 32.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background radial atmospheric neon purple glow
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val brush = Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = if (isRunning) pulseGlowAlpha * 0.25f else 0.05f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.minDimension / 1.5f
                    )
                    drawCircle(brush = brush, radius = size.minDimension / 2f)
                }

                // Clock Canvas (Circular Ring Progress)
                Canvas(
                    modifier = Modifier
                        .size(230.dp)
                        .testTag("circular_progress_ring")
                ) {
                    val strokeWidth = 14.dp.toPx()
                    // Track background arc
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        style = Stroke(width = strokeWidth)
                    )

                    // Active depleting Progress neon ring
                    drawArc(
                        color = NeonPurple,
                        startAngle = -270f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Secondary neon highlight overlay for glowing aura
                    drawArc(
                        color = NeonPurple.copy(alpha = 0.4f),
                        startAngle = -270f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = strokeWidth + 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Central Clock Digits
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-1).sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sessionType.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (sessionType == "Focus Session") NeonPurpleGlow else MintGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Timer Controls deck
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Replay/Reset Action
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("reset_timer_button"),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.05f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Timer",
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Primary Play/Pause Glow Deck
            Button(
                onClick = { viewModel.toggleTimer() },
                modifier = Modifier
                    .size(72.dp)
                    .testTag("play_pause_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Play",
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Ambient audio controls and volume toggling
            Box {
                IconButton(
                    onClick = { isDropdownExpanded = true },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("ambient_sound_selector_button"),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (selectedSound != "None") NeonPurple.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                        contentColor = if (selectedSound != "None") NeonPurpleGlow else Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isAmbientPlaying) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Sound options",
                        modifier = Modifier.size(22.dp)
                    )
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    viewModel.ambientSounds.forEach { sound ->
                        DropdownMenuItem(
                            text = { Text(sound) },
                            onClick = {
                                viewModel.selectAmbientSound(sound)
                                isDropdownExpanded = false
                            },
                            trailingIcon = {
                                if (sound == selectedSound) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = "Selected",
                                        tint = NeonPurple
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ambient Equalizer Controller Card
        if (selectedSound != "None") {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("equalizer_panel"),
                cornerRadius = 20.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            color = NeonPurple.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Ambient Synth",
                                    tint = NeonPurpleGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedSound,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isAmbientPlaying) "Ambient track streaming" else "Chamber Synth muted",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedGrey
                            )
                        }
                    }

                    Switch(
                        checked = isAmbientPlaying,
                        onCheckedChange = { viewModel.toggleAmbientPlaying() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MintGreen,
                            checkedTrackColor = MintGreen.copy(alpha = 0.4f),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("ambient_sound_toggle")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
