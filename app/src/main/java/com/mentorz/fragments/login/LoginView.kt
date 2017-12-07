package com.mentorz.fragments.login

/**
 * Created by aMAN GUPTA on 11/7/17.
 */
interface LoginView {

    fun enterEmail()
    fun enterValidEmail()
    fun enterPassword()
    fun shortPassword()
    fun validLogin()
    fun loginSuccess()
    fun loginFail()
    fun socialLoginFail()
    fun showProgressBar()
    fun hideProgressBar()
    fun openGetStaredActivity()
    fun networkError()
    fun loginErrorAccountFrozen()
    fun updateProfileImage(token: String, fileType: String)

}