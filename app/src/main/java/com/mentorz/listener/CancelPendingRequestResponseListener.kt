package com.mentorz.listener

/**
 * Created by umesh on 30/07/17.
 */
interface CancelPendingRequestResponseListener : SessionExpiredListener,NetworkErrorListener {
    fun onCancelRequestSuccess()
    fun onCancelRequestFail()
}