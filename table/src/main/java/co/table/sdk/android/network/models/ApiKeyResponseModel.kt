package co.table.sdk.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class ApiKeyResponseModel {

    @SerializedName("opentok_api_key")
    @Expose
    var opentokApiKey: String? = null

}