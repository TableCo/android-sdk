package co.table.sdk.android.application

import android.content.Context
import co.table.sdk.android.constants.PrefUtils
import co.table.sdk.android.login.UserModel

internal class Session(val applicationContext: Context) : AppSession {
    companion object {
        private var ourInstance: Session? = null

        fun getInstance(context: Context): Session? {
            if (ourInstance == null) {

                ourInstance = Session(context)
            }
            return ourInstance
        }
    }

    override fun currentUser(): UserModel? {
        return PrefUtils.getUser(applicationContext)
    }

    override fun isAuthenticated(): Boolean {
        return PrefUtils.getUser(applicationContext) != null
    }

    override fun logout() {
        PrefUtils.clearAll(applicationContext)
    }

    override fun saveSession(user: UserModel) {
        PrefUtils.saveUser(applicationContext, user)
    }


}