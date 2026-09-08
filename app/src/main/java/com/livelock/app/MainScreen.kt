package com.livelock.app

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    // 하단 탭 정의 (화면 ↔ 라벨)
    val tabs = listOf(
        Pair(Screen.SOURCE, "소스 선택"),
        Pair(Screen.EDIT, "편집"),
        Pair(Screen.APPLY, "배경 적용"),
        Pair(Screen.SETTINGS, "설정")
    )

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // 상단 탭 바
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (tab in tabs) {
                val screen = tab.first
                val label = tab.second
                Button(
                    onClick = { currentScreen = screen },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(label)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 본문: 현재 화면
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

        Spacer(modifier = Modifier.height(8.dp))
    }
}


