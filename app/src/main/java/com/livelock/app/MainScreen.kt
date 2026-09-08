package com.livelock.app

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.livelock.app.ui.ApplyScreen
import com.livelock.app.ui.EditScreen
import com.livelock.app.ui.SettingsScreen
import com.livelock.app.ui.SourceSelectionScreen

enum class Screen { SOURCE, EDIT, APPLY, SETTINGS }

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(Screen.SOURCE) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }

    when (currentScreen) {
        Screen.SOURCE -> {
            SourceSelectionScreen(
                onImagePicked = { selectedImageUri = it },
                onVideoPicked = { selectedVideoUri = it },
                onAudioPicked = { selectedAudioUri = it }
            )
        }
        Screen.EDIT -> {
            EditScreen(
                videoUri = selectedVideoUri,
                onTrimComplete = { currentScreen = Screen.APPLY }
            )
        }
        Screen.APPLY -> {
            ApplyScreen(
                imageFile = null,
                videoFile = null,
                onSetLockScreen = {},
                onSetHomeScreen = {},
                onOpenNotificationSettings = {}
            )
        }
        Screen.SETTINGS -> {
            SettingsScreen()
        }
    }
}


