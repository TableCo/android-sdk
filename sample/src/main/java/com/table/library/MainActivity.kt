package com.table.library

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import co.table.sdk.android.config.UserParams
import co.table.sdk.android.config.TableLoginCallback
import co.table.sdk.TableSDK


class MainActivity : AppCompatActivity(), TableLoginCallback {
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        tableParams.email = "email@gmail.com"
        tableParams.firstName = "First"
        tableParams.lastName = "Last"

        TableSDK.registerUser("my_user_id", tableParams,this)
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
