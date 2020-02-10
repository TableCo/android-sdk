package com.table.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import co.table.agent.android.config.TBLUserAttributes
import co.table.agent.android.config.TableLoginCallback
import co.table.agent.android.config.TableSDK
import co.table.agent.android.constans.Common

class MainActivity : AppCompatActivity(), TableLoginCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TableSDK.init("https://develop3.dev.table.co","asasasas","asas")
        var tableParams = TBLUserAttributes()
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
