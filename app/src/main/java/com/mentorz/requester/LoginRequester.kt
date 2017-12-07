package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.DeviceInfo
import com.mentorz.model.LoginRequest
import com.mentorz.model.LoginResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.LoginListener
import com.mentorz.utils.Prefs
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 13/7/17.
 */

class LoginRequester(private var presenter: Any?, private var listener: LoginListener?, private var loginRequest: LoginRequest?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.login(loginRequest)
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var loginResponse: LoginResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            loginResponse = MentorzApplication.instance?.gson?.fromJson<LoginResponse>(mentorzApiResponse.responseBody as String, LoginResponse::class.java)
            if (loginResponse != null) {
                listener?.loginSuccess(loginResponse)
            }
            val deviceInfo = DeviceInfo()
            deviceInfo.deviceToken = MentorzApplication.instance?.prefs?.getDeviceToken()
            UpdatePushTokenRequester(deviceInfo, this).execute()


        }  else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
            listener?.loginErrorAccountFrozen()
        }else if (mentorzApiResponse?.statusCode == 0) {
            listener?.onNetworkFail()
        } else {
            listener?.loginUnauthorised()
        }
    }
}

