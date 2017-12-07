package com.mentorz.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

import com.mentorz.R

/**
 * Created by aMAN GUPTA on 8/7/17.
 */

class CustomTextView : TextView {

    private lateinit var myTypeface: Typeface

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {

        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView)
            val fontName = a.getString(R.styleable.CustomTextView_fontName)
            if (fontName != null && fontName.trim { it <= ' ' }.isNotEmpty()) {
                myTypeface = Typeface.createFromAsset(context.assets, "fonts/" + fontName)
            } else {
                myTypeface = Typeface.createFromAsset(context.assets, "fonts/Roboto_Regular.ttf")
            }
            typeface = myTypeface
            a.recycle()
        }
    }

}
