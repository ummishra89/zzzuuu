package com.mentorz.fragments.signupvia

import com.mentorz.listener.NetworkErrorListener

/**
 * Created by aMAN GUPTA on 11/7/17.
 */
class SignupViaPresenterImpl(view: SignupViaView) : SignupViaPresenter,NetworkErrorListener {
    val view = view
    override fun onNetworkFail() {
        view.networkError()
    }


}