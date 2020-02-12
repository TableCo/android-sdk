package co.table.agent.android.config

interface TableLoginCallback {
    fun onSuccessLogin()
    fun onFailure(errorCode:Int)
}