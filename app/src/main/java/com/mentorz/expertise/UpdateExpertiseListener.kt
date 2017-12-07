package com.mentorz.expertise

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by umesh on 30/07/17.
 */
interface UpdateExpertiseListener : SessionExpiredListener , NetworkErrorListener {
    fun onUpdateExpertiseSuccess()
    fun onUpdateExpertiseFail()
}