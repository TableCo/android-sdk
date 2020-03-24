package co.table.sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import co.table.sdk.android.config.*
import co.table.sdk.android.config.TableAuthentication
import co.table.sdk.android.dashboard.DashboardActivity
import co.table.sdk.android.login.RegisterResponseModel
import co.table.sdk.android.network.API
import co.table.sdk.android.network.ApiClient
import co.table.sdk.android.session.ActivityLifecycleWatcher
import co.table.sdk.android.session.AppSession
import co.table.sdk.android.session.Session
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class TableSDK private constructor() {

    companion object {
        private var isDefaultLauncher: Boolean = false
        private var tableAuthentication: TableAuthentication = TableAuthentication()
        private val activityLifecycleWatcher = ActivityLifecycleWatcher()
        private var initialApplicationContext: Context? = null;
        internal val appSession: AppSession = Session()

        internal fun getTableData(): TableAuthentication {
            return tableAuthentication
        }

        // Get the Application Context from the currently shown activity if available, otherwise remember the one we were initialised with
        internal val applicationContext: Context? get() {
            return if (activityLifecycleWatcher.currentActivity?.applicationContext != null) {
                activityLifecycleWatcher.currentActivity?.applicationContext
            } else {
                initialApplicationContext
            }
        }

        //TODO: Move user hash into the register user methods
        fun init(application: Application, workspaceUrl: String, apiKey: String, userHash: String) {
            tableAuthentication.workspaceUrl = workspaceUrl
            tableAuthentication.apiKey = apiKey
            tableAuthentication.userHashMAC = userHash
            initialApplicationContext = application.applicationContext
            application.registerActivityLifecycleCallbacks(activityLifecycleWatcher)
        }

        fun registerUser(userID: String) {
            tableAuthentication.userID = userID
        }

        fun registerUser(userID: String, userHash: String) {
            tableAuthentication.userID = userID
            tableAuthentication.userHashMAC = userHash
        }

        fun registerUser(userID: String, userHash: String, userAttributes: TBLUserAttributes, tableLoginCallback: TableLoginCallback) {
            tableAuthentication.userID = userID
            tableAuthentication.userHashMAC = userHash
            userAttributes.userId = userID
            userAttributes.apiKey = tableAuthentication.apiKey
            userAttributes.userHash = tableAuthentication.userHashMAC
            tableAuthentication.userAttributes = userAttributes

            if (TextUtils.isEmpty(tableAuthentication.workspaceUrl)) {
                tableLoginCallback.onFailure(TABLE_ERROR_NO_WORKSPACE_ADDED)
            } else if (TextUtils.isEmpty(tableAuthentication.apiKey)) {
                tableLoginCallback.onFailure(TABLE_ERROR_API_KEY_EMPTY)
            } else if (TextUtils.isEmpty(tableAuthentication.userHashMAC)) {
                tableLoginCallback.onFailure(TABLE_ERROR_HASH_MAC_EMPTY)
            } else if (TextUtils.isEmpty(tableAuthentication.userID)) {
                tableLoginCallback.onFailure(TABLE_ERROR_USER_ID_EMPTY)
            } else if (TextUtils.isEmpty(tableAuthentication.userAttributes.firstName)) {
                tableLoginCallback.onFailure(TABLE_ERROR_FIRST_NAME_EMPTY)
            } else if (TextUtils.isEmpty(tableAuthentication.userAttributes.lastName)) {
                tableLoginCallback.onFailure(TABLE_ERROR_LAST_NAME_EMPTY)
            } else if (TextUtils.isEmpty(tableAuthentication.userAttributes.email)) {
                tableLoginCallback.onFailure(TABLE_ERROR_EMAIL_EMPTY)
            } else {
                if (appSession.isAuthenticated()){
                    tableLoginCallback.onFailure(TABLE_ERROR_ALL_READY_REGISTERED)
                }else{
                    register(
                        getTableData().userAttributes,
                        API.AUTH_USER,
                        tableLoginCallback
                    )
                }
            }
        }

        fun updateUser(userAttributes: TBLUserAttributes) {
            userAttributes.userId = tableAuthentication.userID
            userAttributes.apiKey = tableAuthentication.apiKey
            userAttributes.userHash = tableAuthentication.userHashMAC
            tableAuthentication.userAttributes = userAttributes
        }

        fun registerUnidentifiedUser() {

        }

        fun useDefaultLauncher(isDefaultLauncher: Boolean) {
            Companion.isDefaultLauncher = isDefaultLauncher
        }

        fun showConversationList(context: Context) {
            if (appSession.isAuthenticated()){
                context.startActivity(Intent(context,DashboardActivity::class.java))
            }
        }

        fun logout() {
            appSession.logout()
        }

        private fun register(
            params: TBLUserAttributes,
            apiTag: String,
            tableLoginCallback: TableLoginCallback
        ) {
            ApiClient().getRetrofitObject(tableAuthentication.workspaceUrl, null).register(params)
                .enqueue(object : Callback,
                    retrofit2.Callback<RegisterResponseModel> {
                    override fun onFailure(call: Call<RegisterResponseModel>, t: Throwable) {
                        tableLoginCallback.onFailure(TABLE_ERROR_NETWORK_FAILURE)
                    }

                    override fun onResponse(call: Call<RegisterResponseModel>, responseModel: Response<RegisterResponseModel>) {
                        if (responseModel.code() == 200) {
                            val registerResponse = responseModel.body()!!
                            registerResponse.user?.let {
                                it.workspace = tableAuthentication.workspaceUrl
                                appSession.saveSession(it)
                                tableLoginCallback.onSuccessLogin()
                            }
                        } else {
                            tableLoginCallback.onFailure(TABLE_ERROR_NETWORK_FAILURE)
                        }
                    }
                })
        }


    }

}