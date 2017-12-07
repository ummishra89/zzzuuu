package com.mentorz.fragments.signup

/**
 * Created by aMAN GUPTA on 11/7/17.
 */
interface SignupView {
    fun enterName()
    fun enterEmail()
    fun enterValidEmail()
    fun enterPassword()
    fun shortPassword()
    fun validSignUp()
    fun alreadyRegister()
    fun showProgressBar()
    fun hideProgressBar()
    fun socialRegistrationSuccess()
    fun openGetStaredActivity()
    fun networkError()
    fun loginErrorAccountFrozen()
}