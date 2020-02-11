package co.table.agent.android.constans

import android.content.Context
import android.content.SharedPreferences
import co.table.agent.android.login.UserModel
import com.google.gson.Gson

internal class PrefUtils {
    companion object {
        @Throws(ClassCastException::class)
        fun getString(
            context: Context, prefKey: String,
            defValue: String?
        ): String? {

            val prefs = getPreferences(context)

            return prefs.getString(prefKey, defValue)
        }

        @Throws(ClassCastException::class)
        fun getDefaultString(
            context: Context, prefKey: String,
            defValue: String
        ): String? {

            val prefs = getDefaultPreferences(context)

            return prefs.getString(prefKey, defValue)
        }

        fun saveString(
            context: Context, prefKey: String,
            prefString: String?
        ): Boolean {


            val editor = getPreferences(context).edit()

            editor.putString(prefKey, prefString)
            return editor.commit()
        }

        fun saveDefaultString(
            context: Context, prefKey: String,
            prefString: String
        ): Boolean {


            val editor = getDefaultPreferences(context).edit()

            editor.putString(prefKey, prefString)
            return editor.commit()
        }

        fun getBoolean(
            context: Context, prefKey: String,
            defValue: Boolean
        ): Boolean {

            val prefs = getPreferences(context)

            return prefs.getBoolean(prefKey, defValue)
        }

        fun getDefaultBoolean(
            context: Context, prefKey: String,
            defValue: Boolean
        ): Boolean {

            val prefs = getDefaultPreferences(context)

            return prefs.getBoolean(prefKey, defValue)
        }


        fun saveInt(context: Context, prefKey: String, value: Int): Boolean {
            val editor = getPreferences(context).edit()

            editor.putInt(prefKey, value)
            return editor.commit()
        }


        fun getInt(context: Context, prefKey: String, defValue: Int): Int {

            val prefs = getPreferences(context)

            return prefs.getInt(prefKey, defValue)
        }

        fun saveLong(context: Context, prefKey: String, value: Long): Boolean {
            val editor = getPreferences(context).edit()

            editor.putLong(prefKey, value)
            return editor.commit()
        }


        fun getLong(context: Context, prefKey: String, defValue: Long): Long {

            val prefs = getPreferences(context)

            return prefs.getLong(prefKey, defValue)
        }

        fun saveBoolean(
            context: Context, prefKey: String,
            value: Boolean
        ): Boolean {
            val editor = getPreferences(context).edit()

            editor.putBoolean(prefKey, value)
            return editor.commit()
        }

        fun saveDefaultBoolean(
            context: Context, prefKey: String,
            value: Boolean
        ): Boolean {
            val editor = getDefaultPreferences(context).edit()

            editor.putBoolean(prefKey, value)
            return editor.commit()
        }

        fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                getPreferencesName(context),
                Context.MODE_PRIVATE
            )
        }

        fun getDefaultPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                getDefaultPreferencesName(context),
                Context.MODE_PRIVATE
            )
        }

        fun getPreferencesName(context: Context): String {
            return context.packageName
        }

        fun getDefaultPreferencesName(context: Context): String {
            return context.packageName + "_default"
        }

        fun clearAll(context: Context) {
            val editor = getPreferences(context).edit()
            editor.clear()
            editor.commit()
        }

        fun saveUser(context: Context, userModel: UserModel): Boolean {
            val editor = getPreferences(context).edit()
            val str = Gson().toJson(userModel)
            editor.putString(Constants.PREF_USER, str)
            return editor.commit()
        }

        fun getUser(context: Context): UserModel? {
            val prefs = getPreferences(context)
            val str = prefs.getString(Constants.PREF_USER, null)
            return if (str != null) {
                Gson().fromJson<UserModel>(str.toString(), UserModel::class.java!!)
            } else {
                null
            }
        }


    }
}
