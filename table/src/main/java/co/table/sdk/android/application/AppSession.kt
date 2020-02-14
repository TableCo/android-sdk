package co.table.sdk.android.application

import co.table.sdk.android.login.UserModel

internal interface AppSession {
    fun currentUser(): UserModel?
    fun isAuthenticated(): Boolean
    fun logout()
    fun saveSession(user: UserModel)
}