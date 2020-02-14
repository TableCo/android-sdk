package co.table.sdk

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import co.table.sdk.android.application.TableApplication
import co.table.sdk.android.config.*
import co.table.sdk.android.config.TableAuthentication
import co.table.sdk.android.dashboard.DashboardActivity
import co.table.sdk.android.login.UserModel
import co.table.sdk.android.network.API
import co.table.sdk.android.network.ApiClient
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class TableSDK {
    companion object {
        private var isDefaultLauncher: Boolean = false
        private var tableAuthentication: TableAuthentication =
            TableAuthentication()
        internal fun getTableData(): TableAuthentication {
            return tableAuthentication
        }

        fun init(workspaceUrl: String, apiKey: String, userHash: String) {
            tableAuthentication.workspaceUrl = workspaceUrl
            tableAuthentication.apiKey = apiKey
            tableAuthentication.userHashMAC = userHash
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
                if (TableApplication.getAppSession().isAuthenticated()){
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
            if (TableApplication.getAppSession().isAuthenticated()){
                context.startActivity(Intent(context,DashboardActivity::class.java))
            }
        }

        fun logout() {
            TableApplication.getAppSession().logout()
        }

        private fun register(
            params: TBLUserAttributes,
            apiTag: String,
            tableLoginCallback: TableLoginCallback
        ) {
            ApiClient().getRetrofitObject(tableAuthentication.workspaceUrl, null).register(params)
                .enqueue(object : Callback,
                    retrofit2.Callback<UserModel> {
                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        tableLoginCallback.onFailure(TABLE_ERROR_NETWORK_FAILURE)
                    }

                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.code() == 200) {
                            val user = response.body()!!
                            user.workspace = tableAuthentication.workspaceUrl
                            TableApplication.getAppSession().saveSession(user)
                            tableLoginCallback.onSuccessLogin()
                        } else {
                            tableLoginCallback.onFailure(TABLE_ERROR_NETWORK_FAILURE)
                        }
                    }
                })
        }
    }

}