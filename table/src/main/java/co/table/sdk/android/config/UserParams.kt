package co.table.sdk.android.config

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

//TODO: Split this up so that there's a user-facing and internal serialised side
class UserParams {
    @SerializedName("user_id")
    @Expose
    internal var userId: String? = ""
    @SerializedName("email")
    @Expose
    var email: String? = ""
    @SerializedName("api_key")
    @Expose
    internal var apiKey: String? = ""
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
    var custom_attributes: HashMap<String, Any> = HashMap()

}