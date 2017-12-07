package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.RequestStatusResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.RequestStatusListener
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 28/07/17.
 */
class RequestStatusRequester(userId: Long?, model:Any?,private var listener: RequestStatusListener?) : BaseRequester() {
    private var mentorId: Long? = userId
    private var model:Any?=model

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getRequestStatus(mentorId)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var response: RequestStatusResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            response = MentorzApplication.instance?.gson?.fromJson<RequestStatusResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), RequestStatusResponse::class.java)
            if (response != null) {
                listener?.requestStatusSuccess(response,model, mentorId)
            }
            return
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.requestStatusFail()
        }
    }

}