package com.devpupu.pixelclock.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotifListener : NotificationListenerService() {
    companion object {
        @Volatile var count = 0
    }
    override fun onListenerConnected() { super.onListenerConnected(); refresh() }
    override fun onNotificationPosted(sbn: StatusBarNotification?) { refresh() }
    override fun onNotificationRemoved(sbn: StatusBarNotification?) { refresh() }
    private fun refresh() { count = activeNotifications?.size ?: 0 }
}
