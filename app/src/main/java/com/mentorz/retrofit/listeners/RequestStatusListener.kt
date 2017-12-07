package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.model.RequestStatusResponse

/**
 * Created by craterzone on 28/07/17.
 */
interface RequestStatusListener : SessionExpiredListener,NetworkErrorListener {
    fun requestStatusSuccess(response: RequestStatusResponse, model:Any?,mentorId: Long?)
    fun requestStatusFail()
}