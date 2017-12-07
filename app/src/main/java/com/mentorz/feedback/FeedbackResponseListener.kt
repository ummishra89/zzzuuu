package com.mentorz.feedback

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by umesh on 09/08/17.
 */
interface FeedbackResponseListener : SessionExpiredListener, NetworkErrorListener {
    fun onFeedbackSuccess()
    fun onFeedbackFail()
}