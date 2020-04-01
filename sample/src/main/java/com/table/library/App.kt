package com.table.library

import android.app.Application
import co.table.sdk.TableSDK

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TableSDK.init(this, "https://YOUR_WORKSPACE.table.co","api_key", "experience_short_code")

    }

}