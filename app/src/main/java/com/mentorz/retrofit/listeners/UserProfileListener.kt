package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.model.ProfileData

/**
 * Created by aMAN GUPTA on 22/7/17.
 */
interface UserProfileListener : SessionExpiredListener,NetworkErrorListener {

    fun userProfileFail() {

    }

    fun userProfileSuccess(userListItem: ProfileData) {

    }

}