package co.table.sdk.android.config

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class UserParams {
    var email: String = ""
    var userHash: String = ""
    var firstName: String? = ""
    var lastName: String? = ""
    var custom_attributes: Map<String, Any> = HashMap()
}