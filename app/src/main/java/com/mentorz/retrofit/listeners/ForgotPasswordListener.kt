package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by aMAN GUPTA on 19/07/17.
 */
interface ForgotPasswordListener : SessionExpiredListener ,NetworkErrorListener{
    fun forgotPasswordSuccess()
    fun forgotPasswordFail()
}