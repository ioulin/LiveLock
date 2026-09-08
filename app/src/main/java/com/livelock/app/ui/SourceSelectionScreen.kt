package com.livelock.app.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMedia
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SourceSelectionScreen(
    onImagePicked: (Uri) -> Unit,
    onVideoPicked: (Uri) -> Unit,
    onAudioPicked: (Uri) -> Unit
) {
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    var selectedAudio by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedImage = it; onImagePicked(it) }
    }
    val videoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedVideo = it; onVideoPicked(it) }
    }
    val audioPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { selectedAudio = it; onAudioPicked(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("LiveLock - 배경 소스 선택")
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedImage != null) "이미지 선택됨 ✓" else "이미지 선택")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                videoPicker.launch(
                    PickVisualMediaRequest(PickVisualMedia.VideoOnly)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedVideo != null) "동영상 선택됨 ✓" else "동영상 선택")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { audioPicker.launch(arrayOf("audio/*")) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedAudio != null) "오디오 선택됨 ✓" else "오디오 선택 (MP3/OGG)")
        }
    }
}

