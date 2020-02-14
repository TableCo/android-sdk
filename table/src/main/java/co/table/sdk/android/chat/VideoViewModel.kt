package co.table.sdk.android.chat

import androidx.lifecycle.MutableLiveData
import co.table.sdk.android.application.TableApplication
import co.table.sdk.android.jetpack.viewmodel.ObservableViewModel
import co.table.sdk.android.login.UserModel
import co.table.sdk.android.network.ApiClient
import co.table.sdk.android.network.ApiResponseInterface
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

internal class VideoViewModel : ObservableViewModel() {
    var userModel = MutableLiveData<UserModel>()
    var apiKey = MutableLiveData<Int>()
    fun getApiKey(apiTag: String, responseInterface: ApiResponseInterface) {
        ApiClient().getRetrofitObject(
            TableApplication.getAppSession().currentUser()?.workspace,
            null
        ).getApiKey().enqueue(object : Callback,
            retrofit2.Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                responseInterface.onFailureRetrofit(t.localizedMessage, apiTag)
            }

            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.code() == 200) {
                    apiKey.value = (response.body() as Double).toInt()
                    responseInterface.onSuccess(response.body(), apiTag)
                } else {
                    responseInterface.onFailureDueToServer("", apiTag)
                }
            }
        })
    }

}
