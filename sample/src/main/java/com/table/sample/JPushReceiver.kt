package com.table.sample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.jpush.android.api.JPushInterface
import co.table.sdk.TableSDK

open class JPushReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val TAG = "JPushReceiver";

        val bundle = intent!!.extras

        if (JPushInterface.ACTION_REGISTRATION_ID == intent.action) {
            val regId = bundle!!.getString(JPushInterface.EXTRA_REGISTRATION_ID)
            if (regId != null && regId.isNotEmpty()) {
                TableSDK.updateJPushRegistrationId(regId)
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED == intent.action) {
            sendJPushBroadcastToMainActivity(context!!, bundle!!)
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED == intent.action) {
            openMainActivity(context!!, bundle!!)
        }
    }

    private fun openMainActivity(context: Context, bundle: Bundle) {
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivityIntent.putExtra(MainActivity.JPUSH_NOTIFICATION, bundle)
        context.startActivity(mainActivityIntent)
    }

    private fun sendJPushBroadcastToMainActivity(context: Context, bundle: Bundle) {
        val mainActivityIntent = Intent()
        mainActivityIntent.action = MainActivity.JPUSH_NOTIFICATION_INTENT_FILTER
        mainActivityIntent.putExtra(MainActivity.JPUSH_NOTIFICATION, bundle)
        context.sendBroadcast(mainActivityIntent)
    }


}