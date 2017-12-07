package com.mentorz.interest

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.retrofit.MentorzApiResponse

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
interface UpdateInterestListener : SessionExpiredListener, NetworkErrorListener {
    fun onUpdateInterestSuccess(mentorzApiResponse: MentorzApiResponse)
    fun onUpdateInterestFail()
}