package com.mentorz.requester

import com.mentorz.model.ForgotPasswordRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.ForgotPasswordListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 19/07/17.
 */
class ForgotPasswordRequester(private var presenter: Any?, private var listener: ForgotPasswordListener?, private var forgotPasswordRequest: ForgotPasswordRequest?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.forgotPassword(forgotPasswordRequest)
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            listener?.forgotPasswordSuccess()

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.forgotPasswordFail()
        }
    }
}

