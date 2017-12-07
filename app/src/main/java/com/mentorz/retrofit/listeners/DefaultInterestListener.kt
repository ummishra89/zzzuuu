package com.mentorz.retrofit.listeners

import com.mentorz.interest.InterestResponse
import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

interface DefaultInterestListener : SessionExpiredListener ,NetworkErrorListener{
    fun onDefaultInterestSuccess(interestResponse: InterestResponse)
    fun onDefaultInterestFail()

}