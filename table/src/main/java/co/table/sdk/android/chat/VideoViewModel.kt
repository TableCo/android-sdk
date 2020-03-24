package co.table.sdk.android.chat

import androidx.lifecycle.MutableLiveData
import co.table.sdk.TableSDK
import co.table.sdk.android.jetpack.viewmodel.ObservableViewModel
import co.table.sdk.android.login.UserModel
import co.table.sdk.android.network.ApiClient
import co.table.sdk.android.network.ApiResponseInterface
import co.table.sdk.android.network.models.ApiKeyResponseModel
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

internal class VideoViewModel : ObservableViewModel() {
    var userModel = MutableLiveData<UserModel>()
    var apiKey = MutableLiveData<String>()
    fun getApiKey(apiTag: String, responseInterface: ApiResponseInterface) {
        ApiClient().getRetrofitObject(
                TableSDK.appSession.currentUser()?.workspace,
            null
        ).getApiKey().enqueue(object : Callback,
            retrofit2.Callback<ApiKeyResponseModel> {
            override fun onFailure(call: Call<ApiKeyResponseModel>, t: Throwable) {
                responseInterface.onFailureRetrofit(t.localizedMessage, apiTag)
            }

            override fun onResponse(
                call: Call<ApiKeyResponseModel>,
                response: Response<ApiKeyResponseModel>
            ) {
                if (response.code() == 200) {
                    apiKey.value = response.body()?.opentokApiKey
                    responseInterface.onSuccess(response.body(), apiTag)
                } else {
                    responseInterface.onFailureDueToServer("", apiTag)
                }
            }
        })
    }

}
