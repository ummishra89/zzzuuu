package com.mentorz.fragments.forgotpassword

import android.text.TextUtils
import com.mentorz.model.ForgotPasswordRequest
import com.mentorz.requester.ForgotPasswordRequester
import com.mentorz.retrofit.listeners.ForgotPasswordListener

/**
 * Created by aMAN GUPTA on 11/7/17.
 */

class ForgotPasswordPresenterImpl(view: ForgotPasswordView) : ForgotPasswordPresenter, ForgotPasswordListener {

    override fun onSessionExpired() {

    }
    override fun onNetworkFail() {
        view.hideProgressBar()
        view.networkError()
    }

    override fun forgotPasswordSuccess() {
        view.validEmail()
        view.hideProgressBar()
    }

    override fun forgotPasswordFail() {
        view.enterNotFount()
        view.hideProgressBar()
    }


    val view: ForgotPasswordView = view

    override fun validateEmail(email: String) {
        if (TextUtils.isEmpty(email)) {
            view.enterEmail()
        } else if (!isValidEmailAddress(email)) {
            view.enterValidEmail()
        } else {
            val forgotPasswordRequest = ForgotPasswordRequest(email)
            val forgotPasswordRequester = ForgotPasswordRequester(this, this, forgotPasswordRequest)
            forgotPasswordRequester.execute()
            view.showProgressBar()
        }

    }

    fun isValidEmailAddress(email: String): Boolean {
        val ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }
}