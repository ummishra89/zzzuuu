package com.mentorz.login

import android.os.Handler
import android.text.TextUtils

/**
 * Created by craterzone on 08/07/17.
 */
class LoginInteractorImpl : LoginInteractor {
    override fun login(username: String, password: String, listener: LoginInteractor.OnLoginFinishedListener) {
        Handler().postDelayed(Runnable {
            var error = false
            if (TextUtils.isEmpty(username)) {
                listener.onUsernameError()
                error = true
                return@Runnable
            }
            if (TextUtils.isEmpty(password)) {
                listener.onPasswordError()
                error = true
                return@Runnable
            }
            if (!error) {
                listener.onSuccess()
            }
        }, 100)
    }

}