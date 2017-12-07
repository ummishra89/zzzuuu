package com.mentorz.requester

import com.mentorz.model.RegistrationRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.RegistrationListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 13/7/17.
 */

class RegistrationRequester(private var presenter: Any?, private var listener: RegistrationListener?, private var registrationRequest: RegistrationRequest?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.register(registrationRequest)
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_CREATED) {
            listener?.registerSuccess()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.registerConflict()
        }
    }
}

