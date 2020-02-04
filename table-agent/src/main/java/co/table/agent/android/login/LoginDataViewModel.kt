package co.table.agent.android.login

import androidx.lifecycle.MutableLiveData
import co.table.agent.android.jetpack.viewmodel.ObservableViewModel
import co.table.agent.android.network.ApiClient
import co.table.agent.android.network.ApiResponseInterface
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LoginDataViewModel : ObservableViewModel() {
    var workspace = MutableLiveData<String>()
    var validWorkspace = MutableLiveData<String>()
    var emailError = MutableLiveData<String>()
    var passwordError = MutableLiveData<String>()
    var serverError = MutableLiveData<String>()

    fun validWorkSpace(): String {
        var workSpace = workspace.value.toString()
        if (!workSpace.contains("http")) {
            workSpace = "https://" + workSpace
        }
        if (!workSpace.contains(".")) {
            workSpace = workSpace + ".table.co"
        }

        validWorkspace.value = workSpace

        return validWorkspace.value.toString()
    }
    fun login(params: LoginRequest, apiTag: String, responseInterface: ApiResponseInterface) {
        ApiClient().getRetrofitObject(validWorkspace.value,null).login(params).enqueue(object : Callback,
            retrofit2.Callback<UserModel> {
            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                responseInterface.onFailureRetrofit(t.localizedMessage, apiTag)
            }

            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.code() == 200) {
                    responseInterface.onSuccess(response.body(), apiTag)
                } else {
                    var userModel = Gson().fromJson<UserModel>(
                        response.errorBody()?.string(),
                        UserModel::class.java
                    )
                    responseInterface.onFailureDueToServer(userModel.message, apiTag)
                }
            }
        })
    }

    fun googleSignIn(params: LoginRequest, apiTag: String, responseInterface: ApiResponseInterface) {
        ApiClient().getRetrofitObject(validWorkspace.value,null).googleSignIn(params).enqueue(object : Callback,
            retrofit2.Callback<UserModel> {
            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                responseInterface.onFailureRetrofit(t.localizedMessage, apiTag)
            }

            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.code() == 200) {
                    responseInterface.onSuccess(response.body(), apiTag)
                } else {
                    var userModel = Gson().fromJson<UserModel>(
                        response.errorBody()?.string(),
                        UserModel::class.java
                    )
                    responseInterface.onFailureDueToServer(userModel.message, apiTag)
                }
            }
        })
    }
}
