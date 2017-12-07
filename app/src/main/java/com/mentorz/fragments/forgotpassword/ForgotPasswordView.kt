package com.mentorz.fragments.forgotpassword

/**
 * Created by aMAN GUPTA on 11/7/17.
 */
interface ForgotPasswordView {
    fun enterEmail()
    fun enterValidEmail()
    fun validEmail()
    fun enterNotFount()
    fun showProgressBar()
    fun hideProgressBar()
    fun networkError()
}