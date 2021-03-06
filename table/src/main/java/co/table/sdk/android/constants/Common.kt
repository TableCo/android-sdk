package co.table.sdk.android.constants

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import co.table.sdk.android.R
import co.table.sdk.android.config.*
import java.util.regex.Pattern

internal object Common {
    private var progressDialog: ProgressDialog? = null

    fun getTypeface(context: Context?, id: Int): Typeface {
        var fontName = "Roboto-Regular.ttf"
        when (id) {
            0 -> {
                fontName = "Roboto-Light.ttf"
            }
            1 -> {
                fontName = "Roboto-Regular.ttf"
            }
            2 -> {
                fontName = "Roboto-Medium.ttf"
            }
            3 -> {
                fontName = "Roboto-Bold.ttf"
            }
            4 -> {
                fontName = "Roboto-Thin.ttf"
            }
            5 -> {
                fontName = "Roboto-Black.ttf"
            }
        }
        var typeface =
            Typeface.createFromAsset(context?.assets, fontName)
        return typeface
    }

    fun isEmail(email: String): Boolean {
        val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        return if (emailPattern.matcher(email).find()) true else false
    }

    fun showProgressDialog(context: Context) {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            progressDialog = ProgressDialog(context)
            progressDialog!!.setMessage(context.resources.getString(R.string.pls_wait))
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideKeyboard(context: Context) {
        val activity = context as Activity
        val inputManager = context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focus = activity.currentFocus
        if (focus != null) {
            inputManager.hideSoftInputFromWindow(
                focus.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun showKeyboard(context: Context) {
        val activity = context as Activity
        val inputManager = context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focus = activity.currentFocus
        if (focus != null) {
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun startInstalledAppDetailsActivity(context: Context?) {
        if (context == null) {
            return
        }
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(i)
    }

    fun errorMessageFromConstant(constant: Int): String {
        return when (constant) {
            TABLE_ERROR_NO_WORKSPACE_ADDED -> "No workspace URL supplied"
            TABLE_ERROR_API_KEY_EMPTY -> "No API key supplied"
            TABLE_ERROR_USER_ID_EMPTY -> "User ID not supplied"
            TABLE_ERROR_FIRST_NAME_EMPTY -> "First name field is empty"
            TABLE_ERROR_LAST_NAME_EMPTY -> "Last name field is empty"
            TABLE_ERROR_EMAIL_EMPTY -> "Email address field is empty"
            TABLE_ERROR_NETWORK_FAILURE -> "Network error"
            TABLE_ERROR_ALL_READY_REGISTERED -> "User is already registered"
            TABLE_ERROR_GENERAL -> "General error"
            else -> "Unknown error"
        }
    }

}