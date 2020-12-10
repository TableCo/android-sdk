package co.table.sdk.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class GetTableResponseModel {

    @SerializedName("id")
    @Expose
    var tableId: String? = null

}