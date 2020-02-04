package co.table.agent.android.application

import co.table.agent.android.login.UserModel

interface AppSession {
    fun currentUser(): UserModel?
    fun isAuthenticated(): Boolean
    fun logout()
    fun saveSession(user: UserModel)
}