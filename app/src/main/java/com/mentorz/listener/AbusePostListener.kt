package com.mentorz.listener

/**
 * Created by umesh on 30/07/17.
 */
interface AbusePostListener : SessionExpiredListener,NetworkErrorListener {
    fun onAbusePostSuccess()
    fun onAbusePostFail()
}