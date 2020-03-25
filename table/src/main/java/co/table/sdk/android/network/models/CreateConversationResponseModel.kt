package co.table.sdk.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class CreateConversationResponseModel {

    @SerializedName("id")
    @Expose
    var conversationId: String? = null

}