package com.table.sample

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.JPushInterface
import co.table.sdk.TableSDK
import co.table.sdk.android.config.TableLoginCallback
import co.table.sdk.android.config.UserParams
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject


class MainActivity : AppCompatActivity(), TableLoginCallback {
    companion object {
        const val EXTRA_REMOTE_MESSAGE = "remote_message"
        const val NOTIFICATION_INTENT_FILTER = "notification_intent_filter"
        const val JPUSH_NOTIFICATION_INTENT_FILTER = "jpush_notification_intent_filter"
        const val JPUSH_NOTIFICATION = "jpush_notification"
    }

    private var progressDialog: ProgressDialog? = null
    private var firebaseBroadcastReceiver: BroadcastReceiver? = null
    private var jPushBroadcastReceiver: BroadcastReceiver? = null
    private var jPushReceiver: JPushReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
        val regId = JPushInterface.getRegistrationID(this);
        if (regId != null && regId.isNotEmpty()) {
            TableSDK.updateJPushRegistrationId(regId)
        }

        val jpushNotification = intent.getBundleExtra(JPUSH_NOTIFICATION)
        if (jpushNotification != null) {
            handleJPushNotification(jpushNotification, this)
        }

        // See if we were launched from a notification while the app was in the background
        intent.extras?.let {
            //This deals with Firebase Notifications
            if (TableSDK.isTablePushMessage(it)) {
                // Let's ask the user if they'd like to deal with it first
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Incoming Message")
                alert.setMessage("You have a new support message from our staff")
                alert.setPositiveButton("Read it") { _, _ ->
                    TableSDK.showConversation(it)
                }
                alert.setNeutralButton("Cancel") { _, _ -> }
                alert.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter(NOTIFICATION_INTENT_FILTER)
        firebaseBroadcastReceiver = object : BroadcastReceiver() {

            // This is where we get informed of new message from MyFirebaseMessagingService
            override fun onReceive(context: Context, intent: Intent) {
                // Get message from intent
                val firebaseMessage = intent.getParcelableExtra<RemoteMessage>(EXTRA_REMOTE_MESSAGE)
                firebaseMessage?.let {
                    if (TableSDK.isTablePushMessage(it)) {
                        // Let's ask the user if they'd like to deal with it first
                        val alert = AlertDialog.Builder(context)
                        alert.setTitle("Incoming Message")
                        alert.setMessage("You have a new support message from our staff")
                        alert.setPositiveButton("Read it") { _, _ ->
                            TableSDK.showConversation(it)
                        }
                        alert.setNeutralButton("Cancel") { _, _ -> }
                        alert.show()
                    }
                }
            }
        }

//        registerReceiver(firebaseBroadcastReceiver, intentFilter)


        //This handles the broadcast from the JPushReceiver to the main activity.
        //We need to do this because you cannot show a dialog from the JPushReceiver
        jPushBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val jpushNotification = intent.getBundleExtra(JPUSH_NOTIFICATION)
                handleJPushNotification(jpushNotification, context)
            }
        }

        jPushReceiver = JPushReceiver()
        //This is the receiver for getting the notifications from JPush
        registerReceiver(jPushReceiver, IntentFilter(JPUSH_NOTIFICATION_INTENT_FILTER))

        //This is the receiver for the broadcast we send from the JPushReceiver
        registerReceiver(jPushBroadcastReceiver, IntentFilter(JPUSH_NOTIFICATION_INTENT_FILTER))
    }

    override fun onPause() {
        super.onPause()
        //We can either register the JPush receivers, or the firebase receiver, if we have both multiple dialogs will appear
        unregisterReceiver(jPushBroadcastReceiver)
        jPushBroadcastReceiver = null
        unregisterReceiver(jPushReceiver)
        jPushReceiver = null
//        unregisterReceiver(firebaseBroadcastReceiver)
//        firebaseBroadcastReceiver = null
    }

    fun handleJPushNotification(bundle: Bundle?, context: Context) {
        bundle?.let {
            val jPushExtras = it.get(JPushInterface.EXTRA_EXTRA)
            if (jPushExtras != null && jPushExtras is String) {
                //If we have jPushExtras, then we're looking at a JPush Notification
                val jsonFromString = Gson().fromJson(jPushExtras, JsonObject::class.java)
                if (TableSDK.isTablePushMessage(jsonFromString)) {
                    // Let's ask the user if they'd like to deal with it first
                    val alert = AlertDialog.Builder(context)
                    alert.setTitle("Incoming Message")
                    alert.setMessage("You have a new support message from our staff")
                    alert.setPositiveButton("Read it") { _, _ ->
                        TableSDK.showConversation(jsonFromString)
                    }
                    alert.setNeutralButton("Cancel") { _, _ -> }
                    alert.show()
                }
            }
        }
    }

    fun onLaunch(view: View) {
        TableSDK.showConversationList(this)
    }

    fun onRegisterAnonymous(view: View) {
        showProgressDialog(this)
        TableSDK.registerUnidentifiedUser(this)
    }

    fun onRegisterUser(view: View) {
        showProgressDialog(this)

        val tableParams = UserParams()
        tableParams.email = "app-user-@gmail.com"
        tableParams.firstName = "Your"
        tableParams.lastName = "User"

        TableSDK.registerUser("USER_ID", tableParams,this)
    }

    override fun onSuccessLogin() {
        dismissProgressDialog()
    }

    override fun onFailure(errorCode: Int, details: String) {
        dismissProgressDialog()

        // Let the user know
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registration Error")
        builder.setMessage("Error code $errorCode - $details")
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }

    private fun showProgressDialog(context: Context) {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            progressDialog = ProgressDialog(context)
            progressDialog!!.setMessage(context.resources.getString(co.table.sdk.android.R.string.pls_wait))
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
