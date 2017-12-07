package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener

/**
 * Created by aMAN GUPTA on 19/07/17.
 */
interface SocialRegistrationListener:NetworkErrorListener{
    fun socialRegistrationSuccess()
    fun SocialRegistrationConflict()
}