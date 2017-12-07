package com.mentorz.requester

import com.mentorz.model.SendMentorRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.SendMentorRequestListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 28/07/17.
 */
class SendMentorRequestRequester(userId:Long,sendMentorRequest: SendMentorRequest, private var listener: SendMentorRequestListener?, private var model: Any?, private var position: Int) : BaseRequester() {

    private var sendMentorRequest: SendMentorRequest? = sendMentorRequest
    private var userId =userId


    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse? = null
            mentorzApiResponse = HttpController.sendMentorRequest(sendMentorRequest, userId)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.sendMentorRequestSuccess(model, position)

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.sendMentorRequestFail()
        }
    }

}