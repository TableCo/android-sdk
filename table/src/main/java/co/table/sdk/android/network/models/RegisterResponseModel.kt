package co.table.sdk.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class RegisterResponseModel {
    @SerializedName("user")
    @Expose
    var user: UserResponseModel? = null
}
