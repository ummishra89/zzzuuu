package com.mentorz.listener

/**
 * Created by umesh on 09/08/17.
 */
interface LogoutResponseListener : SessionExpiredListener,NetworkErrorListener {
    fun onLogoutSuccess()
    fun onLogoutFail()
}