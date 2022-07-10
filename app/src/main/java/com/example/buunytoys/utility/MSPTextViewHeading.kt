package com.example.buunytoys.utility

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextViewHeading(context: Context, attrs: AttributeSet) : AppCompatTextView(context,attrs){
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "Polaris-Regular.ttf")
        setTypeface(typeface)
    }
}