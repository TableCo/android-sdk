package com.table.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import co.table.sdk.android.config.TBLUserAttributes
import co.table.sdk.android.config.TableLoginCallback
import co.table.sdk.TableSDK
import co.table.sdk.android.constants.Common

class MainActivity : AppCompatActivity(), TableLoginCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tableParams = TBLUserAttributes()
        tableParams.email = "felixthomas727@gmail.com"
        tableParams.firstName = "Felix"
        tableParams.lastName = "Thomas"
        Common.showProgressDialog(this)
        TableSDK.registerUser("asas","aas",tableParams,this)
    }


    fun onLaunch(view: View) {

        TableSDK.showConversationList(this)
    }

    override fun onSuccessLogin() {
        Common.dismissProgressDialog()
    }

    override fun onFailure(errorCode: Int) {
        Common.dismissProgressDialog()
    }

}
