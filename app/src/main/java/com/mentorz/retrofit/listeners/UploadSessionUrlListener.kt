package com.mentorz.retrofit.listeners

/**
 * Created by craterzone on 27/07/17.
 */
interface UploadSessionUrlListener {
    fun uploadSessionUrlSuccess(url: String)
    fun uploadSessionUrlFail()
}