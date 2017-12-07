package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.model.LoginResponse

/**
 * Created by aMAN GUPTA on 18/07/17.
 */
interface SocialLoginListener:NetworkErrorListener {
    fun SocialLoginSuccess(loginResponse: LoginResponse?)
    fun SocialLoginUnauthorized()
    fun loginErrorAccountFrozen()
}