package com.mentorz.fragments.loginsignup

import com.mentorz.listener.NetworkErrorListener

/**
 * Created by aMAN GUPTA on 11/7/17.
 */

class LoginSignupPresenterImpl(view: LoginSignupView) : LoginSignupPresenter ,NetworkErrorListener{
    private val view = view
    override fun onNetworkFail() {
        view.networkError()
    }


}