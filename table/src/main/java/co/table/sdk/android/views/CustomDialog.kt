package co.table.sdk.android.views

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import co.table.sdk.android.R
import co.table.sdk.android.databinding.DialogCustomBinding

internal class CustomDialog(
    context: Context
) :
    AlertDialog(context) {
    private val db: DialogCustomBinding

    fun setTitleVisibility(visibility: Int) {
        db.tvTitle.setVisibility(visibility)
    }

    fun setMsgVisibility(visibility: Int) {
        db.tvMsg.setVisibility(visibility)
    }

    fun setPositiveButtonVisibility(visibility: Int) {
        db.btnPositive.setVisibility(visibility)
        if (visibility == View.GONE) {
            db.btnNegative.setLayoutParams(
                LinearLayout.LayoutParams(
                    context.resources.getDimension(
                        R.dimen.fix_button_width
                    ).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT, 0f
                )
            )
        } else {
            db.btnNegative.setLayoutParams(
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )
        }
    }

    fun setNegativeButtonVisibility(visibility: Int) {
        db.btnNegative.setVisibility(visibility)
        if (visibility == View.GONE) {
            db.btnPositive.setLayoutParams(
                LinearLayout.LayoutParams(
                    context.resources.getDimension(
                        R.dimen.fix_button_width
                    ).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT, 0f
                )
            )
        } else {
            db.btnPositive.setLayoutParams(
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )
        }
    }

    fun setTitle(title: String?) {
        db.tvTitle.setText(title)
    }

    fun setMessege(msg: String?) {
        db.tvMsg.setText(msg)
    }

    fun setPositiveButtonText(text: String?) {
        db.btnPositive.setText(text)
    }

    fun setNegativeButtonText(text: String?) {
        db.btnNegative.setText(text)
    }

    fun setOnPositiveClickListener(clickListener: View.OnClickListener?) {
        db.btnPositive.setOnClickListener(clickListener)
    }

    fun setOnNegativeClickListener(clickListener: View.OnClickListener?) {
        db.btnNegative.setOnClickListener(clickListener)
    }

    fun setHeaderVisibility(visibility: Int) {
        db.llHeader.setVisibility(visibility)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
        db = DataBindingUtil.bind(view)!!
        setView(view)
    }
}