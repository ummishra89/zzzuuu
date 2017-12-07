package com.mentorz.customviews

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.mentorz.R
import kotlinx.android.synthetic.main.custom_alart_dialog.*

/**
 * Created by aMAN GUPTA on 12/7/17.
 */

class CustomAlertDialog(context: Context, title: String, description: String, buttonText: String, listener: CustomAlertDialogListener) : Dialog(context) {


    val title: String = title
    val description: String = description
    val buttonText: String = buttonText
    val listener: CustomAlertDialogListener = listener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_alart_dialog)
        ctv_title.text = title
        ctv_description.text = description
        ctv_button.text = buttonText
        ctv_button.setOnClickListener {
            dismiss()
            listener.onButtonClickListener()
        }
    }

    interface CustomAlertDialogListener {
        fun onButtonClickListener()
    }
}