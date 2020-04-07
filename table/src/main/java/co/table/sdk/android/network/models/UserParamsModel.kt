package co.table.sdk.android.network.models

import co.table.sdk.android.config.UserParams
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.HashMap

internal class UserParamsModel(userParams: UserParams, userId: String?, apiKey: String) {
    @SerializedName("user_id")
    @Expose
    internal var userId: String? = userId

    @SerializedName("email")
    @Expose
    var email: String? = ""

    @SerializedName("api_key")
    @Expose
    internal var apiKey: String? = apiKey

    @SerializedName("user_hash")
    @Expose
    internal var userHash: String? = ""

    @SerializedName("first_name")
    @Expose
    var firstName: String? = ""

    @SerializedName("last_name")
    @Expose
    var lastName: String? = ""

    @SerializedName("custom_attributes")
    @Expose
    var custom_attributes: Map<String, Any> = HashMap()

    init {
        this.userId = userId
        this.apiKey = apiKey
        this.email = userParams.email
        this.userHash = userParams.userHash
        this.firstName = userParams.firstName
        this.lastName = userParams.lastName
        this.custom_attributes = userParams.custom_attributes
    }
}