package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by aMAN GUPTA on 22/7/17.
 */
interface UpdateProfileListener : SessionExpiredListener,NetworkErrorListener {
    fun updateProfileSuccess()
    fun updateProfileFail()
}