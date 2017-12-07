package com.mentorz.login

/**
 * Created by craterzone on 08/07/17.
 */
interface LoginPresenter {
    fun validateCredentials(username: String, password: String)
    fun onDestroy()
}