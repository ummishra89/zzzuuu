package com.mentorz.requester

import com.mentorz.model.ResetPasswordRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.ResetPasswordListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class ResetPasswordRequester(private var presenter: Any?, private var listener: ResetPasswordListener?, passwordRequest: ResetPasswordRequest?) : BaseRequester() {
    private var resetPasswordRequest: ResetPasswordRequest? = passwordRequest

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.resetPassword(resetPasswordRequest)
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            listener?.resetPasswordSuccess()

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.resetPasswordFail()
        }
    }
}

