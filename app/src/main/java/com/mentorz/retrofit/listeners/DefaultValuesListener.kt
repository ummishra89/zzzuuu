package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.model.ValuesResponse

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
interface DefaultValuesListener : SessionExpiredListener,NetworkErrorListener {
    fun defaultValuesSuccess(valuesResponse: ValuesResponse)
    fun defaultValuesFail()
}