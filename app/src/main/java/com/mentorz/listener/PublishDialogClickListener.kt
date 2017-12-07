package com.mentorz.listener

import android.net.Uri

/**
 * Created by umesh on 23/07/17.
 */
interface PublishDialogClickListener  {
    fun onPublish(fileType:String, uri: Uri, comment:String)
    fun onImageClick(text:String)
    fun onVideoPublishClick(text:String)

}