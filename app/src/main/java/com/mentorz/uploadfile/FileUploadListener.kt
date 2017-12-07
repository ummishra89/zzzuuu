package com.mentorz.uploadfile

import com.mentorz.listener.SessionExpiredListener

/**
 * Created by craterzone on 27/07/17.
 */
interface FileUploadListener : SessionExpiredListener {
    fun fileUploadSuccess(token: String, fileType: String)
    fun fileUploadFail()
    fun publishProgress(progress:Int,fileType: String){

    }
    fun fileUploadSuccess(url: String){

    }
}