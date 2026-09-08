package com.livelock.app.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livelock.app.media.VideoTrimmer
import java.io.File

@Composable
fun EditScreen(
    videoUri: Uri?,
    onTrimComplete: (File) -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var trimDuration by remember { mutableFloatStateOf(15f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("동영상 편집")
        Spacer(modifier = Modifier.height(16.dp))

        Text("길이: ${trimDuration.toInt()}초 (최대 15초)")
        Slider(
            value = trimDuration,
            onValueChange = { trimDuration = it },
            valueRange = 1f..15f,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isProcessing) {
            LinearProgressIndicator(progress = progress)
            Text("처리 중...")
        } else {
            Button(
                onClick = {
                    videoUri?.let { uri ->
                        isProcessing = true
                        val outputFile = File.createTempFile("trimmed", ".mp4")
                        VideoTrimmer.trim(
                            context = androidx.compose.ui.platform.LocalContext.current,
                            inputUri = uri,
                            outputFile = outputFile,
                            maxDurationMs = (trimDuration * 1000).toLong()
                        ) { success ->
                            isProcessing = false
                            if (success) onTrimComplete(outputFile)
                        }
                    }
                },
                enabled = videoUri != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("트림 & 인코딩 시작")
            }
        }
    }
}
