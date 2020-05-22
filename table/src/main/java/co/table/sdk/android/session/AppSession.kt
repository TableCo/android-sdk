package co.table.sdk.android.session

import co.table.sdk.android.network.models.UserResponseModel

internal interface AppSession {
    fun currentUser(): UserResponseModel?
    fun isAuthenticated(): Boolean
    fun logout()
    fun saveSession(user: UserResponseModel)
    fun updateFcmToken(token: String, channel: String?)
    fun updateJPushRegistrationId(registrationId: String, channel: String?)
}