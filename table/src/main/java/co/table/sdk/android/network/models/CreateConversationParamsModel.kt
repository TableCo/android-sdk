package co.table.sdk.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class CreateConversationParamsModel(
        @SerializedName("experience_short_code")
        @Expose var experienceShortCode: String?
)
