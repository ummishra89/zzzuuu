package com.mentorz.fragments.login

import com.mentorz.model.SocialLoginRequest

/**
 * Created by aMAN GUPTA on 11/7/17.
 */

interface LoginPresenter {
    fun validateLogin(email: String, password: String)
    fun loginApi(email: String, password: String)
    fun socialLoginApi(socialLoginRequest: SocialLoginRequest)
}