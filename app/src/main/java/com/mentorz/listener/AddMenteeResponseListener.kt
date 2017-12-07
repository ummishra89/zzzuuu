package com.mentorz.listener

/**
 * Created by umesh on 30/07/17.
 */
interface AddMenteeResponseListener : SessionExpiredListener,NetworkErrorListener {
    fun onAcceptRequestSuccess()
    {

    }
    fun onAcceptRequestFail(){

    }

}