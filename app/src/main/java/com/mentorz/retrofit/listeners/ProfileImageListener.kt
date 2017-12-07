package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.model.ProfileImageResponse

/**
 * Created by craterzone on 08/08/17.
 */
interface ProfileImageListener : SessionExpiredListener ,NetworkErrorListener{
    fun profileImageSuccess(profileImageResponse: ProfileImageResponse, userId: Long?)
    fun profileImageFail()
}