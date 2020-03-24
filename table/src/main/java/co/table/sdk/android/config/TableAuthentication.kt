package co.table.sdk.android.config

internal class TableAuthentication {
    // Members which don't need to reset
    internal var workspaceUrl = ""
    internal var apiKey = ""

    // Members to reset on logout
    internal var userID: String? = ""
    internal var userParams: UserParams = UserParams()
}