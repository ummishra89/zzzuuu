package com.mentorz.login

/**
 * Created by craterzone on 08/07/17.
 */
interface LoginInteractor {
    interface OnLoginFinishedListener {
        fun onUsernameError()
        fun onPasswordError()
        fun onSuccess()
    }

    fun login(username: String, password: String, listener: OnLoginFinishedListener)
}