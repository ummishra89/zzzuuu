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
class ChooseImageDialog(context: Context, val listener: ChooseImageDialogListener) : Dialog(context), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_cancel -> {
                listener.onCancelClickListener()
            }
            R.id.tv_camera -> {
                listener.onCameraClickListener()
            }
            R.id.tv_gallery -> {
                listener.onGalleryClickListener()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_choose_image)
        tv_cancel.setOnClickListener(this)
        tv_camera.setOnClickListener(this)
        tv_gallery.setOnClickListener(this)


    }

    interface ChooseImageDialogListener {
        fun onCancelClickListener()
        fun onGalleryClickListener()
        fun onCameraClickListener()
    }
}