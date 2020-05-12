package com.table.sample

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import co.table.sdk.TableSDK


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Create a notification channel for the Table SDK
        var channel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                getString(R.string.table_notification_channel),
                getString(R.string.table_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            if (getSystemService(Context.NOTIFICATION_SERVICE) != null) {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        TableSDK.init(this, "https://YOUR_WORKSPACE.table.co","your_api_key", null, getString(R.string.table_notification_channel))
    }

}