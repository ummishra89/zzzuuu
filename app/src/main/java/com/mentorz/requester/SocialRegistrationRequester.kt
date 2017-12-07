package com.mentorz.requester

import com.mentorz.model.SocialRegistrationRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.SocialRegistrationListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 19/07/17.
 */
class SocialRegistrationRequester(private var presenter: Any?, private var listener: SocialRegistrationListener?, registrationRequest: SocialRegistrationRequest?) : BaseRequester() {
    private var socialRegistrationRequest: SocialRegistrationRequest? = registrationRequest

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.socialRegister(socialRegistrationRequest)
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_CREATED) {
            listener?.socialRegistrationSuccess()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.SocialRegistrationConflict()
        }
    }
}