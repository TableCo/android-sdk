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
import co.table.sdk.TableSDK
import co.table.sdk.android.config.TableLoginCallback
import co.table.sdk.android.config.UserParams
import com.google.firebase.messaging.RemoteMessage


class MainActivity : AppCompatActivity(), TableLoginCallback {
    companion object {
        const val EXTRA_REMOTE_MESSAGE = "remote_message"
        const val NOTIFICATION_INTENT_FILTER = "notification_intent_filter"
    }

    private var progressDialog: ProgressDialog? = null
    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // See if we were launched from a notification while the app was in the background
        intent.extras?.let {
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
        broadcastReceiver = object : BroadcastReceiver() {

            // This is where we get informed of new FCM messages from MyFirebaseMessagingService
            override fun onReceive(context: Context, intent: Intent) {
                // Get message from intent
                val message = intent.getParcelableExtra<RemoteMessage>(EXTRA_REMOTE_MESSAGE)
                message?.let {
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

                    // Deal with app-specific messages or messages from other services here
                }
            }
        }

        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
        broadcastReceiver = null
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
        tableParams.email = "gazreese+user@gmail.com"
        tableParams.firstName = "Gazreese"
        tableParams.lastName = "User"

        TableSDK.registerUser("gazreese+user", tableParams,this)
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
