package com.livelock.app.notify

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationReactionService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return
        val packageName = sbn.packageName ?: return
        // TODO: 앱별/키워드별 반응 규칙 평가 후 월페이퍼에 이벤트 전달
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
