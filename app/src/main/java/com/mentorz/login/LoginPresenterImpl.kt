package com.mentorz.login

import com.mentorz.listener.NetworkErrorListener

open class LoginPresenterImpl(loginView: LoginView) : LoginPresenter, LoginInteractor.OnLoginFinishedListener,NetworkErrorListener {

    override fun onNetworkFail() {
        loginView?.hideProgress()
        loginView?.networkError()
    }

    private var loginView: LoginView? = null
    private val loginInteractor: LoginInteractor

    init {
        this.loginView = loginView
        this.loginInteractor = LoginInteractorImpl()
    }

    override fun validateCredentials(username: String, password: String) {

        if (loginView != null) {
            loginView!!.showProgress()
        }
        loginInteractor.login(username, password, listener = this)
    }

    override fun onUsernameError() {
        if (loginView != null) {
            loginView!!.setUsernameError()
            loginView!!.hideProgress()
        }
    }

    override fun onDestroy() {
        loginView = null
    }

    override fun onPasswordError() {
        if (loginView != null) {
            loginView!!.setPasswordError()
            loginView!!.hideProgress()
        }
    }

    override fun onSuccess() {
        if (loginView != null) {
            loginView!!.navigateToHome()
        }
    }


}
