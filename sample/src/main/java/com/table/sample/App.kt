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

        TableSDK.init(this, "https://YOUR_WORKSPACE.table.co","your_api_key", null, getString(R.string.table_notification_channel))
    }

}