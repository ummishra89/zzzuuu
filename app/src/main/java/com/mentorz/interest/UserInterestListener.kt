package com.mentorz.interest

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

interface UserInterestListener : SessionExpiredListener , NetworkErrorListener {
    fun onUserInterestSuccess(valuesResponse: InterestResponse)
    fun onUserInterestFail()
}