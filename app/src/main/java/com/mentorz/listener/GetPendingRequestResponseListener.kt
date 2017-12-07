package com.mentorz.listener

import com.mentorz.messages.RequestListItem

/**
 * Created by umesh on 30/07/17.
 */
interface GetPendingRequestResponseListener : SessionExpiredListener ,NetworkErrorListener{
    fun onGetPendingRequestSuccess(requestList: MutableList<RequestListItem>)
    fun onGetPendingRequestFail()
    fun pendingRequestNotFound()
}