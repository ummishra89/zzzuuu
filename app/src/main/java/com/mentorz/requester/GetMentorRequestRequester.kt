package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.listener.MentorRequestsListener
import com.mentorz.messages.PendingRequestResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class GetMentorRequestRequester(private var pageNo: Int, private var listener: MentorRequestsListener?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getMentorRequests(pageNo)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var pendingRequestResponse:PendingRequestResponse?=null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            pendingRequestResponse = MentorzApplication.instance?.gson?.fromJson<PendingRequestResponse>(mentorzApiResponse.responseBody as String, PendingRequestResponse::class.java)
            if (pendingRequestResponse != null) {
                listener?.onMentorsRequestSuccess()
            }
        }
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onMentorRequestFail()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onMentorRequestFail()
        }
    }


}