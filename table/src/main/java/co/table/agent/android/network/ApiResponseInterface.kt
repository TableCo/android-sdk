package co.table.agent.android.network

interface ApiResponseInterface {
    fun onSuccess(successResponse: Any?, apiTag: String)
    fun onFailureDueToServer(errorMessage: Any?,apiTag: String)
    fun onFailureRetrofit(message: String?,apiTag: String)
    fun logoutUser()
    fun noDataFound(apiTag: String)
}