package co.table.sdk.android.account

import androidx.lifecycle.MutableLiveData
import co.table.sdk.android.application.TableApplication
import co.table.sdk.android.dashboard.HeaderResponseModel
import co.table.sdk.android.jetpack.viewmodel.ObservableViewModel
import co.table.sdk.android.login.UserModel
import co.table.sdk.android.network.ApiClient
import co.table.sdk.android.network.ApiResponseInterface
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

internal class ConversationSettingsViewModel : ObservableViewModel() {
    var userModel = MutableLiveData<UserModel>()
    var headerResponseModel = MutableLiveData<HeaderResponseModel>()

    fun getHeaderDetails(tableId: String, apiTag: String, responseInterface: ApiResponseInterface) {
        ApiClient().getRetrofitObject(
            TableApplication.getAppSession().currentUser()?.workspace,
            null
        ).getHeader(tableId).enqueue(object : Callback,
            retrofit2.Callback<HeaderResponseModel> {
            override fun onFailure(call: Call<HeaderResponseModel>, t: Throwable) {
                responseInterface.onFailureRetrofit(t.localizedMessage, apiTag)
            }

            override fun onResponse(
                call: Call<HeaderResponseModel>,
                response: Response<HeaderResponseModel>
            ) {
                if (response.code() == 200) {
                    headerResponseModel.value = response.body()
                    responseInterface.onSuccess(response.body(), apiTag)
                } else {
                    responseInterface.onFailureDueToServer("", apiTag)
                }
            }
        })
    }
}
