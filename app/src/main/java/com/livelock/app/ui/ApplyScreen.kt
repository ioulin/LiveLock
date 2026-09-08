package com.livelock.app.ui

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.livelock.app.wallpaper.WallpaperApplier
import java.io.File

@Composable
fun ApplyScreen(
    imageFile: File?,
    videoFile: File?,
    onSetLockScreen: () -> Unit,
    onSetHomeScreen: () -> Unit,
    onOpenNotificationSettings: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("배경 적용")
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                imageFile?.let { WallpaperApplier.setLockScreen(context, it) }
                onSetLockScreen()
            },
            enabled = imageFile != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("잠금화면 적용 (정적)")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                imageFile?.let { WallpaperApplier.setHomeScreen(context, it) }
                onSetHomeScreen()
            },
            enabled = imageFile != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("홈화면 적용 (정적)")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                context.startActivity(intent)
                onOpenNotificationSettings()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("알림 접근 권한 설정")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val intent = Intent("android.settings.WALLPAPER_SETTINGS")
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("원UI 배경화면 설정 열기 (동영상)")
        }
    }
}
