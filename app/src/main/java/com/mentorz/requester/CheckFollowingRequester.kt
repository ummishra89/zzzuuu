package com.mentorz.requester

import com.google.gson.annotations.SerializedName
import com.mentorz.MentorzApplication
import com.mentorz.listener.FollowListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class CheckFollowingRequester(userId:Long?,model: Any?, private var presenter: Any?, private var listener: FollowListener?) : BaseRequester() {
    private var model: Any? = model
    private var userId:Long?=userId

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.checkFollowing(userId)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var mentorResponse: Response? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            mentorResponse = MentorzApplication.instance?.gson?.fromJson<Response>(mentorzApiResponse.responseBody as String, Response::class.java)
            if (mentorResponse != null) {
                listener?.checkFollowingSuccess(model,mentorResponse.value == 1)
            }
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else{
            listener?.checkFollowingFail()
        }
    }

    inner class Response(@field:SerializedName("value")
                         val value: Int? = 0)


}