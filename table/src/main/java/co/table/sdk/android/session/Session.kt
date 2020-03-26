package co.table.sdk.android.session

import android.content.Context
import co.table.sdk.TableSDK
import co.table.sdk.TableSDK.Companion.applicationContext
import co.table.sdk.android.constants.PrefUtils
import co.table.sdk.android.network.models.UserResponseModel

internal class Session() : AppSession {
    companion object {
        private var ourInstance: Session? = null

        fun getInstance(): Session? {
            if (ourInstance == null) {
                ourInstance = Session()
            }
            return ourInstance
        }
    }

    override fun currentUser(): UserResponseModel? {
        val context = applicationContext
        return if (context != null) {
            PrefUtils.getUser(context)
        } else {
            null;
        }
    }

    override fun isAuthenticated(): Boolean {
        val context = applicationContext
        return if (context != null) {
            PrefUtils.getUser(context) != null
        } else {
            false
        }
    }

    override fun logout() {
        applicationContext?.let{ PrefUtils.clearAll(it) }
    }

    override fun saveSession(user: UserResponseModel) {
        applicationContext?.let{ PrefUtils.saveUser(it, user) }
    }

}