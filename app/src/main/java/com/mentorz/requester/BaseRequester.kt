package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.model.DeviceInfo
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.UpdatePushTokenListener
import com.mentorz.utils.Prefs
import javax.net.ssl.HttpsURLConnection

/**
 * Created by craterzone on 05/07/17.
 */
abstract class BaseRequester : UpdatePushTokenListener {
    override fun onNetworkFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSessionExpired() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun isSessionExpired(listener: SessionExpiredListener?, mentorzApiResponse: MentorzApiResponse?): Boolean {

        if (mentorzApiResponse?.statusCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
            listener?.onSessionExpired()
            return true
        } else {
            return false
        }
    }
}