package com.mentorz.requester

import com.mentorz.match.BeMentorListener
import com.mentorz.match.BeMentorRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class BeMentorRequester(beMentorRequest: BeMentorRequest, private var presenter: Any?, private var listener: BeMentorListener?) : BaseRequester() {
    private var beMentorRequest: BeMentorRequest? = beMentorRequest

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.beMentor(beMentorRequest!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            listener?.onBeMentorSuccess()
            return
        }
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_CREATED) {
            listener?.onMentorCreated()
            return
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onBeMentorFail()
        }
    }


}