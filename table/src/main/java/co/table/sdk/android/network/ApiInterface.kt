package co.table.sdk.android.network

import co.table.sdk.android.config.UserParams
import co.table.sdk.android.dashboard.HeaderResponseModel
import co.table.sdk.android.login.LoginRequest
import co.table.sdk.android.login.RegisterResponseModel
import co.table.sdk.android.login.UserModel
import co.table.sdk.android.network.models.ApiKeyResponseModel
import co.table.sdk.android.network.models.TokenRequestModel
import co.table.sdk.android.network.models.TokenResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface ApiInterface {
    @POST(API.LOGIN)
    fun login(@Body params: LoginRequest): Call<UserModel>

    @POST(API.GOOGLE_SIGNIN)
    fun googleSignIn(@Body params: LoginRequest): Call<UserModel>

    @GET(API.GET_HEADER + "{tableId}")
    fun getHeader(@Path("tableId") tableId: String): Call<HeaderResponseModel>

    @GET(API.GET_API_KEY)
    fun getApiKey(): Call<ApiKeyResponseModel>

    @POST(API.ADD_FCM_DEVICE_TOKEN)
    fun sendToken(@Body params: TokenRequestModel): Call<TokenResponseModel>

    @POST(API.AUTH_USER)
    fun register(@Body params: UserParams): Call<RegisterResponseModel>
}