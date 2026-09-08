package com.livelock.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livelock.app.notify.ReactionRule
import com.livelock.app.notify.ReactionRuleEngine

@Composable
fun SettingsScreen() {
    var rules by remember { mutableStateOf(listOf<ReactionRule>()) }
    var soundEnabled by remember { mutableStateOf(true) }
    var bubbleEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("설정")
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("효과음 재생")
            Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("알림 말풍선 표시")
            Switch(checked = bubbleEnabled, onCheckedChange = { bubbleEnabled = it })
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("반응 규칙")
        Spacer(modifier = Modifier.height(8.dp))

        rules.forEach { rule ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${rule.appName} → 애니메이션 ${rule.animationIndex}")
                Switch(checked = rule.enabled, onCheckedChange = {})
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: 규칙 추가 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("규칙 추가")
        }
    }
}
