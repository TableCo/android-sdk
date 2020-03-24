package co.table.sdk.android.dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName





internal class HeaderResponseModel {
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
    var deleted: Any? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("last_activity")
    @Expose
    var lastActivity: String? = null
    @SerializedName("archived")
    @Expose
    var archived: Any? = null
    @SerializedName("properties")
    @Expose
    var properties: Properties? = null
    @SerializedName("items")
    @Expose
    var items: List<Any>? = null
    @SerializedName("members")
    @Expose
    var members: List<Member>? = null

}
internal class Member{
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
    var deleted: Any? = null
    @SerializedName("table_id")
    @Expose
    var tableId: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("accepted")
    @Expose
    var accepted: Boolean? = null
    @SerializedName("crew_member_id")
    @Expose
    var crewMemberId: String? = null
    @SerializedName("last_visit")
    @Expose
    var lastVisit: String? = null
    @SerializedName("is_owner")
    @Expose
    var isOwner: Boolean? = null
//    @SerializedName("profile")
//    @Expose
//    var profile: Profile? = null
}
internal class Properties{
    @SerializedName("list_id")
    @Expose
    var listId: String? = null
}


