package com.table.sample

import android.util.Log
import co.table.sdk.TableSDK
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.intercom.android.sdk.push.IntercomPushClient




class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val intercomPushClient = IntercomPushClient()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val message = remoteMessage.data
        Log.d("TableSample", "onMessageReceived - $message")
        if (intercomPushClient.isIntercomPush(message))

        super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        TableSDK.updateFcmToken(token)

        super.onNewToken(token)
    }

}