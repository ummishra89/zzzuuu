package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.retrofit.MentorzApiResponse

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
interface UpdateValuesListener : SessionExpiredListener,NetworkErrorListener {
    fun onUpdateValuesSuccess(mentorzApiResponse: MentorzApiResponse)
    fun onUpdateValuesFail()
}