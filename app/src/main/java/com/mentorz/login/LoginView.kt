package com.mentorz.login

/**
 * Created by craterzone on 08/07/17.
 */
interface LoginView {
    fun showProgress()
    fun hideProgress()
    fun setUsernameError()
    fun setPasswordError()
    fun navigateToHome()
    fun networkError()
}