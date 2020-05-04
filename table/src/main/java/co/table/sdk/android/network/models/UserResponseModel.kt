package co.table.sdk.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class UserResponseModel {
//    @SerializedName("user")
//    @Expose
//    var profile: Profile? = null
    @SerializedName("token")
    @Expose
    var token: String? = null
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
    @SerializedName("experience_short_code")
    @Expose
    var experienceShortCode: String? = null
    @SerializedName("fcm_notification_channel")
    @Expose
    var fcmNotificationChannel: String? = null
}