package com.mentorz.requester

import com.mentorz.listener.AddMenteeResponseListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class AddMenteeRequester(menteeId: Long, private var listener: AddMenteeResponseListener?) : BaseRequester() {
    private var menteeId: Long? = menteeId

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.addMentee(menteeId!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onAcceptRequestSuccess()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onAcceptRequestFail()
        }
    }


}