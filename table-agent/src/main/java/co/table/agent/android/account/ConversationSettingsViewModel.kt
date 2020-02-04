package co.table.agent.android.account

import androidx.lifecycle.MutableLiveData
import co.table.agent.android.application.TableApplication
import co.table.agent.android.dashboard.HeaderResponseModel
import co.table.agent.android.jetpack.viewmodel.ObservableViewModel
import co.table.agent.android.login.UserModel
import co.table.agent.android.network.ApiClient
import co.table.agent.android.network.ApiResponseInterface
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ConversationSettingsViewModel : ObservableViewModel() {
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
