package com.mentorz.match

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by umesh on 25/07/17.
 */
interface BeMentorListener : SessionExpiredListener , NetworkErrorListener {
    fun onBeMentorSuccess()
    fun onBeMentorFail()
    fun onMentorCreated()
}