package com.table.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import co.table.sdk.android.config.UserParams
import co.table.sdk.android.config.TableLoginCallback
import co.table.sdk.TableSDK
import co.table.sdk.android.constants.Common

class MainActivity : AppCompatActivity(), TableLoginCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }


    fun onLaunch(view: View) {
        TableSDK.showConversationList(this)
    }

    fun onRegisterUser(view: View) {
        Common.showProgressDialog(this)

        val tableParams = UserParams()
        tableParams.email = "felixthomas727@gmail.com"
        tableParams.firstName = "Felix"
        tableParams.lastName = "Thomas"

        TableSDK.registerUser("my_user_id", tableParams,this)
    }

    fun onRegisterAnonymous(view: View) {
        Common.showProgressDialog(this)
        TableSDK.registerUnidentifiedUser("anonymous_user_id")
    }

    override fun onSuccessLogin() {
        Common.dismissProgressDialog()
    }

    override fun onFailure(errorCode: Int) {
        Common.dismissProgressDialog()
    }

}
