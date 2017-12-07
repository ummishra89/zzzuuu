package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
interface ResetPasswordListener : SessionExpiredListener,NetworkErrorListener {
    fun resetPasswordSuccess()
    fun resetPasswordFail()
}