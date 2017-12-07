package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by craterzone on 28/07/17.
 */
interface SendMentorRequestListener : SessionExpiredListener,NetworkErrorListener {
    fun sendMentorRequestSuccess(model: Any?, position: Int)
    fun sendMentorRequestFail()
    override fun onNetworkFail() {

    }
}