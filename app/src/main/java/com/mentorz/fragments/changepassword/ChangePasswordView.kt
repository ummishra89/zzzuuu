package com.mentorz.fragments.changepassword

/**
 * Created by aMAN GUPTA on 12/7/17.
 */

interface ChangePasswordView {
    fun enterVerificationCode()
    fun verificationCodeLength()
    fun enterNewPassword()
    fun shortPassword()
    fun enterConfirmPassword()
    fun passwordNotMatch()
    fun invalidVerificationCode()
    fun validLogin()
    fun loginSuccess()
    fun showProgressBar()
    fun hideProgressBar()
    fun openGetStaredActivity()
    fun onSessionExpired()
    fun networkError()
}