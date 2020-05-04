package co.table.sdk.android.session

import android.util.Log
import co.table.sdk.TableSDK.Companion.LOG_TAG
import co.table.sdk.TableSDK.Companion.applicationContext
import co.table.sdk.android.constants.PrefUtils
import co.table.sdk.android.network.ApiClient
import co.table.sdk.android.network.models.TokenRequestModel
import co.table.sdk.android.network.models.TokenResponseModel
import co.table.sdk.android.network.models.UserResponseModel
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

internal class Session() : AppSession {
    companion object {
        private var ourInstance: Session? = null

        fun getInstance(): Session? {
            if (ourInstance == null) {
                ourInstance = Session()
            }
            return ourInstance
        }
    }

    override fun currentUser(): UserResponseModel? {
        val context = applicationContext
        return if (context != null) {
            PrefUtils.getUser(context)
        } else {
            null;
        }
    }

    override fun isAuthenticated(): Boolean {
        val context = applicationContext
        return if (context != null) {
            PrefUtils.getUser(context) != null
        } else {
            false
        }
    }

    override fun logout() {
        applicationContext?.let{ PrefUtils.clearAll(it) }
    }

    override fun saveSession(user: UserResponseModel) {
        applicationContext?.let{ PrefUtils.saveUser(it, user) }
    }

    override fun updateFcmToken(token: String, channel: String?) {
        if (currentUser() == null) {
            Log.d(LOG_TAG, "No current user while trying to update the FCM token")
        }

        val tokenRequestModel = TokenRequestModel()
        tokenRequestModel.fcm_device_token = token
        channel?.let { tokenRequestModel.fcm_notfication_channel = it }

        ApiClient().getRetrofitObject(
            currentUser()!!.workspace,
            currentUser()!!.token
        ).sendToken(
            tokenRequestModel
        ).enqueue(object : Callback,
            retrofit2.Callback<TokenResponseModel> {
            override fun onFailure(call: Call<TokenResponseModel>, t: Throwable) {
                Log.d(LOG_TAG, "FCM token failure error due to server", t)
            }

            override fun onResponse(
                call: Call<TokenResponseModel>,
                response: Response<TokenResponseModel>
            ) {
                if (response.code() == 200) {
                    Log.d(LOG_TAG, "FCM token added to server - $token on channel $channel")
                } else {
                    Log.d(LOG_TAG, "FCM token failure error due to server - ${response.code()}")
                }
            }
        })
    }

}