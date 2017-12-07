package com.mentorz.interest

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

interface DefaultChildInterestListener :SessionExpiredListener, NetworkErrorListener {
    fun onDefaultChildInterestSuccess(valuesResponse: InterestResponse)
    fun onDefaultChildInterestFail()
}