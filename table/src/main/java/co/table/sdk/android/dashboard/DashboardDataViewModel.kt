package co.table.sdk.android.dashboard

import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import co.table.sdk.TableSDK
import co.table.sdk.android.jetpack.viewmodel.ObservableViewModel
import co.table.sdk.android.network.API
import co.table.sdk.android.network.ApiClient
import co.table.sdk.android.network.ApiResponseInterface
import co.table.sdk.android.network.models.CreateConversationParamsModel
import co.table.sdk.android.network.models.CreateConversationResponseModel
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


internal class DashboardDataViewModel : ObservableViewModel() {
    var headerResponseModel = MutableLiveData<HeaderResponseModel>()
    var headerTitle = MutableLiveData<String>()
    var shouldShowNewMessage = MutableLiveData(false)
    @ColorInt var themeColorInt = 0;

    fun getHeader(tableId: String, apiTag: String, responseInterface: ApiResponseInterface) {
        val workspace = TableSDK.appSession.currentUser()?.workspace
        val token = TableSDK.appSession.currentUser()?.token

        if (workspace == null || token == null) {
            responseInterface.onFailureRetrofit("No workspace or token", API.GET_HEADER)
            return
        }

        ApiClient().getRetrofitObject(workspace, token)
                .getHeader(tableId)
                .enqueue(object : Callback,
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

    fun createConversation(responseInterface: ApiResponseInterface) {
        val workspace = TableSDK.appSession.currentUser()?.workspace
        val experienceShortCode = TableSDK.appSession.currentUser()?.experienceShortCode
        val token = TableSDK.appSession.currentUser()?.token

        if (workspace == null || token == null) {
            responseInterface.onFailureRetrofit("No workspace or token", API.CREATE_CONVERSATION)
            return
        }

        ApiClient().getRetrofitObject(workspace, token)
                .createConversation(CreateConversationParamsModel(experienceShortCode))
                .enqueue(object : Callback,
                        retrofit2.Callback<CreateConversationResponseModel> {
                    override fun onFailure(call: Call<CreateConversationResponseModel>, t: Throwable) {
                        responseInterface.onFailureRetrofit(t.localizedMessage, API.CREATE_CONVERSATION)
                    }

                    override fun onResponse(
                            call: Call<CreateConversationResponseModel>,
                            response: Response<CreateConversationResponseModel>
                    ) {
                        if (response.code() == 200) {
                            responseInterface.onSuccess(response.body(), API.CREATE_CONVERSATION)
                        } else {
                            responseInterface.onFailureDueToServer(response.errorBody()?.string(), API.CREATE_CONVERSATION)
                        }
                    }
                })
    }
}
