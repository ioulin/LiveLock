package com.livelock.app.notify

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.livelock.app.audio.SfxPlayer

class NotificationReactionService : NotificationListenerService() {

    private var ruleEngine: ReactionRuleEngine? = null
    private var prefs: SharedPreferences? = null

    override fun onCreate() {
        super.onCreate()
        ruleEngine = ReactionRuleEngine(this)
        prefs = getSharedPreferences("reaction_rules", Context.MODE_PRIVATE)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return
        val packageName = sbn.packageName ?: return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        val animIndex = ruleEngine?.evaluate(packageName, title, text) ?: return
        // 월페이퍼(같은 프로세스)가 감지하도록 저장
        prefs?.edit()?.putInt("last_anim", animIndex)?.putLong("last_time", System.currentTimeMillis())?.apply()
        SfxPlayer.play(this, "sfx_alert.ogg")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        requestRebind(ComponentName(this, NotificationReactionService::class.java))
    }
}

