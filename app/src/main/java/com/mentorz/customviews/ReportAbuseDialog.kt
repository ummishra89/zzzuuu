package com.mentorz.customviews

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.mentorz.R
import kotlinx.android.synthetic.main.report_dialog.*


/**
 * Created by aMAN GUPTA on 24/7/17.
 */
class ReportAbuseDialog(context: Context, val listener: ReportAbuseDialogListener) : Dialog(context), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_cancel -> {
                listener.onAbuseCancelClickListener()
            }
            R.id.tv_spam -> {
                listener.onSpamClickListener()
            }
            R.id.tv_inappropriate_content -> {
                listener.onInAppropriateContentClickListener()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.report_dialog)
        tv_cancel.setOnClickListener(this)
        tv_inappropriate_content.setOnClickListener(this)
        tv_spam.setOnClickListener(this)


    }

    interface ReportAbuseDialogListener {
        fun onAbuseCancelClickListener()
        fun onInAppropriateContentClickListener()
        fun onSpamClickListener()
    }
}