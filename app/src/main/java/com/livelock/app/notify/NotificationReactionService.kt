package com.livelock.app.notify

import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationReactionService : NotificationListenerService() {

    private var ruleEngine: ReactionRuleEngine? = null

    override fun onCreate() {
        super.onCreate()
        ruleEngine = ReactionRuleEngine(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return
        val packageName = sbn.packageName ?: return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        ruleEngine?.evaluate(packageName, title, text)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        requestRebind(android.content.ComponentName(this, NotificationReactionService::class.java))
    }
}

