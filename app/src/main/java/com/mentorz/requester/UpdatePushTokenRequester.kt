package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.DeviceInfo
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.UpdatePushTokenListener
import com.mentorz.utils.Prefs
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 31/08/17.
 */
class UpdatePushTokenRequester(private var deviceInfo: DeviceInfo, private var listener: UpdatePushTokenListener?) : BaseRequester() {
    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.updateFireBasePushToken(deviceInfo)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.IS_PUSH_TOKEN_UPDATED,true)
        }
    }
}