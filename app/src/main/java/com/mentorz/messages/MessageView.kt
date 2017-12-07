package com.mentorz.messages

/**
 * Created by umesh on 12/08/17.
 */
interface MessageView {
    fun showProgress()
    fun hideProgress()
    fun setPendingRequestAdapter(pendingRequestList:MutableList<RequestListItem>)
    fun onPendingRequestFail()
    fun pendingRequestNotFound()
    fun onSessionExpired()
    fun onCancelRequestSuccess()
    fun onCancelRequestFail()
    fun onProfileImageSuccess()
    fun onRejectRequestSuccess()
    fun onRejectRequestFail()
    fun onAcceptRequestSuccess()
    fun onAcceptRequestFail()
    fun networkError()
}