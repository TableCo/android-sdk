package co.table.sdk.android.dashboard

import androidx.lifecycle.MutableLiveData
import co.table.sdk.android.application.TableApplication
import co.table.sdk.android.jetpack.viewmodel.ObservableViewModel
import co.table.sdk.android.network.ApiClient
import co.table.sdk.android.network.ApiResponseInterface
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


internal class DashboardDataViewModel : ObservableViewModel() {
    var headerResponseModel = MutableLiveData<HeaderResponseModel>()
    var headerTitle = MutableLiveData<String>()

    fun getHeader(tableId: String, apiTag: String, responseInterface: ApiResponseInterface) {
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
                    headerTitle.value = headerResponseModel.value?.title
                    responseInterface.onSuccess(response.body(), apiTag)
                } else {
                    responseInterface.onFailureDueToServer("", apiTag)
                }
            }
        })
    }
}
