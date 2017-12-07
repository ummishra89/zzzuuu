package com.mentorz.retrofit.listeners

import com.mentorz.listener.SessionExpiredListener

/**
 * Created by craterzone on 27/07/17.
 */
interface SignedUrlListener : SessionExpiredListener {
    fun signedUrlSuccess(url: String, model: Any?){

    }
    fun signedUrlSuccess(fileType:String,url: String, model: Any?){

    }
    fun signedUrlFail(){}

}