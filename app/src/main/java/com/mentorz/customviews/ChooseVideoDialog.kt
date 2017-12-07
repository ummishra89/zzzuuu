package com.mentorz.customviews

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.mentorz.R
import kotlinx.android.synthetic.main.dialog_choose_image.*

/**
 * Created by aMAN GUPTA on 24/7/17.
 */
class ChooseVideoDialog(context: Context, val listener: ChooseVideoDialogListener) : Dialog(context), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_cancel -> {
               dismiss()
            }
            R.id.tv_camera -> {
                dismiss()
                listener.onCaptureVideo()
            }
            R.id.tv_gallery -> {
                dismiss()
                listener.onSelectFromGallery()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_choose_image)
        tv_camera.text = context.getString(R.string.take_a_new_video)
        tv_cancel.setOnClickListener(this)
        tv_camera.setOnClickListener(this)
        tv_gallery.setOnClickListener(this)


    }

    interface ChooseVideoDialogListener {
        fun onSelectFromGallery()
        fun onCaptureVideo()
    }
}