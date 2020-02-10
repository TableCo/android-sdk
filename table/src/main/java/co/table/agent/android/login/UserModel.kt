package co.table.agent.android.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserModel {
    @SerializedName("user")
    @Expose
    var profile: Profile? = null
    /*@SerializedName("token")
    @Expose
    var token: String? = null*/
    @SerializedName("is_agent")
    @Expose
    var isAgent: Boolean? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("workspace")
    @Expose
    var workspace: String? = null
}

class Profile {
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("created")
    @Expose
    var created: String? = null
    @SerializedName("updated")
    @Expose
    var updated: String? = null
    @SerializedName("deleted")
    @Expose
    var deleted: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("first_name")
    @Expose
    var firstName: String? = null
    @SerializedName("last_name")
    @Expose
    var lastName: String? = null
    @SerializedName("handle")
    @Expose
    var handle: String? = null
    @SerializedName("is_agent")
    @Expose
    var isAgent: Boolean? = null
    @SerializedName("last_seen")
    @Expose
    var lastSeen: String? = null
    @SerializedName("is_admin")
    @Expose
    var isAdmin: String? = null
    @SerializedName("avatar_media_id")
    @Expose
    var avatarMediaId: String? = null
    @SerializedName("is_superadmin")
    @Expose
    var isSuperAdmin: Boolean? = null
    @SerializedName("email_unsubscribe")
    @Expose
    var emailUnsubscribe: String? = null
    @SerializedName("avatar_url")
    @Expose
    var avatarUrl: String? = null
    @SerializedName("token")
    @Expose
    var token: String? = null
}