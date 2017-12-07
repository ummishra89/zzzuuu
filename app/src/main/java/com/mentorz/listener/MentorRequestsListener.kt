package com.mentorz.listener

/**
 * Created by umesh on 30/07/17.
 */
interface MentorRequestsListener : SessionExpiredListener ,NetworkErrorListener{
    fun onMentorsRequestSuccess()
    {

    }
    fun onMentorRequestFail(){

    }

    override fun onNetworkFail() {

    }

}