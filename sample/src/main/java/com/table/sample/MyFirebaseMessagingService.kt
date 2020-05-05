package com.table.sample

import android.content.Intent
import android.util.Log
import co.table.sdk.TableSDK
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val message = remoteMessage.data
        Log.d("TableSample", "onMessageReceived - $message")

        if (TableSDK.isTablePushMessage(remoteMessage)) {
            // Let our MainActivity know about the incoming message and we can deal with it appropriately
            val intent = Intent()
            intent.action = MainActivity.NOTIFICATION_INTENT_FILTER
            intent.putExtra(MainActivity.EXTRA_REMOTE_MESSAGE, remoteMessage)
            sendBroadcast(intent)
        }

        // Deal with app-specific messages or messages from other services here

        super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        TableSDK.updateFcmToken(token)

        super.onNewToken(token)
    }

}