package com.mentorz.expertise

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener


/**
 * Created by umesh on 23/07/17.
 */
interface DefaultExpertiseListener : SessionExpiredListener, NetworkErrorListener {
    fun onDefaultExpertiseSuccess(expertiseResponse: Response)
    fun onDefaultExpertiseFail()
    fun onDefaultChildExpertiseSuccess(expertiseResponse: Response)
    fun onDefaultChildExpertiseFail()
}