package co.table.sdk.android.config

internal class TableAuthentication {
    //Member Which don't need to reset
    internal var workspaceUrl = ""
    internal var apiKey = ""
    internal var userHashMAC = ""

    //Members to reset on logout
    internal var userID:String? = ""
    internal var userAttributes:TBLUserAttributes = TBLUserAttributes()
}