package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.LoginResponse
import com.mentorz.model.SocialLoginRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.SocialLoginListener
import com.mentorz.utils.Prefs
import org.jetbrains.anko.doAsync
import retrofit2.HttpException
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 17/7/17.
 */
class SocialLoginRequester(private var presenter: Any?, private var listener: SocialLoginListener?, private var socialLoginRequest: SocialLoginRequest?) : BaseRequester() {


    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.socialLogin(socialLoginRequest)

            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var loginResponse: LoginResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            loginResponse = MentorzApplication.instance?.gson?.fromJson<LoginResponse>(mentorzApiResponse.responseBody as String, LoginResponse::class.java)
            if (loginResponse != null) {
                listener?.SocialLoginSuccess(loginResponse)
                MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.IS_SOCIAL_LOGIN, true)
            }
        }
        else if(mentorzApiResponse?.statusCode==HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
            listener?.loginErrorAccountFrozen()
        }

        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.SocialLoginUnauthorized()
        }
    }
}
