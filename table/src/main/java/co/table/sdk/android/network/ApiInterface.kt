package co.table.sdk.android.network

import co.table.sdk.android.config.UserParams
import co.table.sdk.android.dashboard.HeaderResponseModel
import co.table.sdk.android.network.models.*
import co.table.sdk.android.network.models.ApiKeyResponseModel
import co.table.sdk.android.network.models.CreateConversationResponseModel
import co.table.sdk.android.network.models.TokenRequestModel
import co.table.sdk.android.network.models.TokenResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface ApiInterface {

    @GET(API.GET_HEADER + "{tableId}")
    fun getHeader(@Path("tableId") tableId: String): Call<HeaderResponseModel>

    @GET(API.GET_API_KEY)
    fun getApiKey(): Call<ApiKeyResponseModel>

    @POST(API.ADD_FCM_DEVICE_TOKEN)
    fun sendToken(@Body params: TokenRequestModel): Call<TokenResponseModel>

    @POST(API.AUTH_USER)
    fun register(@Body params: UserParamsModel): Call<RegisterResponseModel>

    @POST(API.CREATE_CONVERSATION)
    fun createConversation(@Body params: CreateConversationParamsModel): Call<CreateConversationResponseModel>

    @GET(API.GET_INSTALLATION_PROPERTIES)
    fun getInstallationProperties(): Call<InstallationPropertiesResponse>

    @POST(API.ADD_JPUSH_REGISTRATION_ID)
    fun sendJPushRegistrationId(@Body params: JPushRegistrationIdRequestModel): Call<JPushRegistrationIdResponseModel>

}