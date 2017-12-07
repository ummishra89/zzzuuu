package com.mentorz.customviews

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.Window
import com.mentorz.R
import kotlinx.android.synthetic.main.dialog_video_upload.*

/**
 * Created by craterzone on 26/07/17.
 */

class UploadVideoDialog(context: Context, val videoThumbnail: Bitmap, val listener: UploadVideoListener) : Dialog(context), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgClose.id -> {
                listener.onCancelListener()
            }
            btUpload.id -> {
                progress.visibility = View.VISIBLE
                listener.onUploadListener(videoThumbnail)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_video_upload)
        imgClose.setOnClickListener(this)
        btUpload.setOnClickListener(this)
        imgVideoThumbnail.setImageBitmap(videoThumbnail)
    }

    interface UploadVideoListener {
        fun onCancelListener()
        fun onUploadListener(bitmap: Bitmap)
    }
}