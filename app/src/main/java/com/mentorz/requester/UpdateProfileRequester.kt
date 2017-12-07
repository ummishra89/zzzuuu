package com.mentorz.requester

import com.mentorz.model.UpdateProfileRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.UpdateProfileListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 22/7/17.
 */
class UpdateProfileRequester(private var presenter: Any?, private var listener: UpdateProfileListener?, request: UpdateProfileRequest?) : BaseRequester() {
    private var updateProfileRequest: UpdateProfileRequest? = request

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.updateProfile(updateProfileRequest)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.updateProfileSuccess()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.updateProfileFail()
        }
    }
}

