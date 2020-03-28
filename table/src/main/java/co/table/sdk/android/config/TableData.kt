package co.table.sdk.android.config

import android.graphics.Color
import androidx.annotation.ColorInt
import co.table.sdk.android.network.models.UserParamsModel

internal class TableData {
    // Members which don't need to reset
    internal var workspaceUrl = ""
    internal var apiKey = ""
    @ColorInt internal var themeColor = Color.parseColor("#307AEB")

    // Members to reset on logout
    internal var userID: String? = ""
    internal var userParamsModel: UserParamsModel? = null
}