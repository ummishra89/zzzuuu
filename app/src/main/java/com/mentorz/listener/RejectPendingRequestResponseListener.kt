package com.mentorz.listener

/**
 * Created by umesh on 30/07/17.
 */
interface RejectPendingRequestResponseListener : SessionExpiredListener ,NetworkErrorListener{
    fun onRejectRequestSuccess()
    fun onRejectRequestFail()
}