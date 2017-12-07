package com.mentorz.fragments.signup

/**
 * Created by aMAN GUPTA on 11/7/17.
 */
interface SignupPresenter {
    fun validateSignUp(name: String, email: String, password: String, basicInfo: String, birthDay: String, isSocialSignup: Boolean, socialID: String, socialSource: String)
}