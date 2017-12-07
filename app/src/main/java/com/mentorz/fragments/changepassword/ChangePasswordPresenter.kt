package com.mentorz.fragments.changepassword

/**
 * Created by aMAN GUPTA on 12/7/17.
 */
interface ChangePasswordPresenter {
    fun validateLogin(verificationCode: String, newPassword: String, confirmPassword: String)
    fun loginApi(email: String, password: String)
}