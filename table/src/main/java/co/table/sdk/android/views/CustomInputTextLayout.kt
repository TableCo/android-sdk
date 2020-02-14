package co.table.sdk.android.views

import android.content.Context
import android.util.AttributeSet
import co.table.sdk.android.R
import co.table.sdk.android.constants.Common
import com.google.android.material.textfield.TextInputLayout

internal class CustomInputTextLayout : TextInputLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, context)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {
        init(attrs, context)
    }

    private fun init(attrs: AttributeSet?, context: Context?) {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView)
        val fontName = typedArray.getInt(R.styleable.CustomTextView_customFont, 1)
        val customTypeface = Common.getTypeface(context, fontName)
        typeface = customTypeface
    }
}