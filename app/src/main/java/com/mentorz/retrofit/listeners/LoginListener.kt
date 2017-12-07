package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.model.LoginResponse

/**
 * Created by aMAN GUPTA on 17/7/17.
 */
interface LoginListener:NetworkErrorListener {
    fun loginSuccess(loginResponse: LoginResponse?)
    fun loginUnauthorised()
    fun loginErrorAccountFrozen()
}