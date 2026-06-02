package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonPurpleGlow
import com.example.ui.theme.TextMutedGrey
import com.example.viewmodel.ProductivityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ViewModelProvider(this)[ProductivityViewModel::class.java]
            val currentScreen by viewModel.currentScreen.collectAsState()

            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBar(
                            currentScreen = currentScreen,
                            onTabSelected = { viewModel.navigateTo(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                            },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                ProductivityViewModel.Screen.FOCUS -> FocusScreen(viewModel)
                                ProductivityViewModel.Screen.TASKS -> TasksScreen(viewModel)
                                ProductivityViewModel.Screen.NOTES -> NotesScreen(viewModel)
                                ProductivityViewModel.Screen.SETUP -> SetupScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    currentScreen: ProductivityViewModel.Screen,
    onTabSelected: (ProductivityViewModel.Screen) -> Unit
) {
    val items = listOf(
        NavigationItem("Focus", Icons.Default.Timer, ProductivityViewModel.Screen.FOCUS, "focus_tab_btn"),
        NavigationItem("Tasks", Icons.Default.ListAlt, ProductivityViewModel.Screen.TASKS, "tasks_tab_btn"),
        NavigationItem("Notes", Icons.Default.Edit, ProductivityViewModel.Screen.NOTES, "notes_tab_btn"),
        NavigationItem("Setup", Icons.Default.Settings, ProductivityViewModel.Screen.SETUP, "setup_tab_btn")
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = Color.Black.copy(alpha = 0.85f),
        tonalElevation = 8.dp,
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentScreen == item.screen
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(item.screen) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag(item.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) NeonPurpleGlow else TextMutedGrey,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = if (isSelected) NeonPurpleGlow else TextMutedGrey
                    )
                }
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val screen: ProductivityViewModel.Screen,
    val testTag: String
)
