package co.table.sdk.android.config

interface TableLoginCallback {
    fun onSuccessLogin()
    fun onFailure(errorCode:Int, details: String)
}