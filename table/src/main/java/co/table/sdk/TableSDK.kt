package co.table.sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import co.table.sdk.android.config.*
import co.table.sdk.android.config.TableData
import co.table.sdk.android.constants.Common
import co.table.sdk.android.dashboard.DashboardActivity
import co.table.sdk.android.network.ApiClient
import co.table.sdk.android.network.models.InstallationPropertiesResponse
import co.table.sdk.android.network.models.RegisterResponseModel
import co.table.sdk.android.network.models.UserParamsModel
import co.table.sdk.android.session.ActivityLifecycleWatcher
import co.table.sdk.android.session.AppSession
import co.table.sdk.android.session.Session
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class TableSDK private constructor() {

    companion object {
        internal val LOG_TAG = "Table SDK"
        private var isDefaultLauncher: Boolean = false
        private var tableData: TableData = TableData()
        private val activityLifecycleWatcher = ActivityLifecycleWatcher()
        private var initialApplicationContext: Context? = null;
        internal val appSession: AppSession = Session()

        fun init(application: Application, workspaceUrl: String, apiKey: String) {
            tableData.workspaceUrl = workspaceUrl
            tableData.apiKey = apiKey
            initialApplicationContext = application.applicationContext
            application.registerActivityLifecycleCallbacks(activityLifecycleWatcher)

            updateTheme()
        }

        fun registerUnidentifiedUser(userID: String, tableLoginCallback: TableLoginCallback?) {
            registerUser(userID, UserParams(), false, tableLoginCallback)
        }

        fun registerUser(userID: String, userParams: UserParams, tableLoginCallback: TableLoginCallback?) {
            registerUser(userID, userParams, true, tableLoginCallback)
        }

        fun useDefaultLauncher(isDefaultLauncher: Boolean) {
            Companion.isDefaultLauncher = isDefaultLauncher
        }

        fun showConversationList(context: Context) {
            if (appSession.isAuthenticated()) {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.putExtra(DashboardActivity.EXTRA_COLOR_INT, tableData.themeColor)
                context.startActivity(intent)
            }
        }

        fun logout() {
            appSession.logout()
        }

        internal fun getTableData(): TableData {
            return tableData
        }

        // Get the Application Context from the currently shown activity if available, otherwise remember the one we were initialised with
        internal val applicationContext: Context? get() {
            return if (activityLifecycleWatcher.currentActivity?.applicationContext != null) {
                activityLifecycleWatcher.currentActivity?.applicationContext
            } else {
                initialApplicationContext
            }
        }

        private fun register(params: UserParamsModel, tableLoginCallback: TableLoginCallback?) {
            ApiClient().getRetrofitObject(tableData.workspaceUrl, null).register(params)
                .enqueue(object : Callback,
                    retrofit2.Callback<RegisterResponseModel> {
                    override fun onFailure(call: Call<RegisterResponseModel>, t: Throwable) {
                        tableLoginCallback?.onFailure(TABLE_ERROR_NETWORK_FAILURE, Common.errorMessageFromConstant(TABLE_ERROR_NETWORK_FAILURE) + " " + t.localizedMessage)
                    }

                    override fun onResponse(call: Call<RegisterResponseModel>, responseModel: Response<RegisterResponseModel>) {
                        if (responseModel.code() == 200) {
                            val registerResponse = responseModel.body()!!
                            registerResponse.user?.let {
                                it.workspace = tableData.workspaceUrl
                                appSession.saveSession(it)
                                tableLoginCallback?.onSuccessLogin()
                            }
                        } else {
                            tableLoginCallback?.onFailure(TABLE_ERROR_NETWORK_FAILURE, Common.errorMessageFromConstant(TABLE_ERROR_NETWORK_FAILURE) + " " + responseModel.code())
                        }
                    }
                })
        }

        private fun updateTheme() {
            ApiClient().getRetrofitObject(tableData.workspaceUrl, null).getInstallationProperties()
                    .enqueue(object : Callback,
                            retrofit2.Callback<InstallationPropertiesResponse> {
                        override fun onFailure(call: Call<InstallationPropertiesResponse>, t: Throwable) {
                            Log.e(LOG_TAG, "Failed to get installation properties ${t.localizedMessage}", t)
                        }

                        override fun onResponse(call: Call<InstallationPropertiesResponse>, responseModel: Response<InstallationPropertiesResponse>) {
                            if (responseModel.code() == 200) {
                                val installationProperties = responseModel.body()
                                val color = installationProperties?.themeOverrides?.semanticPalette?.asColor
                                color?.let {
                                    Log.i(LOG_TAG, "Got the installation properties theme color $color")
                                    getTableData().themeColor = it
                                }
                            } else {
                                Log.e(LOG_TAG, "Failed to get the installation properties theme color ${responseModel.code()} ${responseModel.errorBody()}")
                            }
                        }
                    })
        }

        private fun registerUser(userID: String, userParams: UserParams, validateUserParams: Boolean, tableLoginCallback: TableLoginCallback?) {
            tableData.userID = userID
            tableData.userParamsModel = UserParamsModel(userParams, userID, tableData.apiKey)

            if (TextUtils.isEmpty(tableData.workspaceUrl)) {
                tableLoginCallback?.onFailure(TABLE_ERROR_NO_WORKSPACE_ADDED, Common.errorMessageFromConstant(TABLE_ERROR_NO_WORKSPACE_ADDED))
            } else if (TextUtils.isEmpty(tableData.apiKey)) {
                tableLoginCallback?.onFailure(TABLE_ERROR_API_KEY_EMPTY, Common.errorMessageFromConstant(TABLE_ERROR_API_KEY_EMPTY))
            } else if (TextUtils.isEmpty(tableData.userID)) {
                tableLoginCallback?.onFailure(TABLE_ERROR_USER_ID_EMPTY, Common.errorMessageFromConstant(TABLE_ERROR_USER_ID_EMPTY))
            } else if (validateUserParams && TextUtils.isEmpty(tableData.userParamsModel?.firstName)) {
                tableLoginCallback?.onFailure(TABLE_ERROR_FIRST_NAME_EMPTY, Common.errorMessageFromConstant(TABLE_ERROR_FIRST_NAME_EMPTY))
            } else if (validateUserParams && TextUtils.isEmpty(tableData.userParamsModel?.lastName)) {
                tableLoginCallback?.onFailure(TABLE_ERROR_LAST_NAME_EMPTY, Common.errorMessageFromConstant(TABLE_ERROR_LAST_NAME_EMPTY))
            } else if (validateUserParams && TextUtils.isEmpty(tableData.userParamsModel?.email)) {
                tableLoginCallback?.onFailure(TABLE_ERROR_EMAIL_EMPTY, Common.errorMessageFromConstant(TABLE_ERROR_EMAIL_EMPTY))
            } else {
                if (getTableData().userParamsModel != null) {
                    register(getTableData().userParamsModel!!, tableLoginCallback)
                } else {
                    tableLoginCallback?.onFailure(TABLE_ERROR_GENERAL, "Inconsistent data during registration")
                }
            }
        }
    }

}