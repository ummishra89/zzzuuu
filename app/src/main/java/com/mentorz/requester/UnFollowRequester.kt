package com.mentorz.requester

import com.mentorz.listener.UnFollowListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

class UnFollowRequester(model: Any?, userId:Long, private var presenter: Any?, private var listener: UnFollowListener?) : BaseRequester() {
    private var model: Any? = model
    private var userId =userId

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.unFollowUser(userId)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {

            listener?.onUnFollowSuccess(model)
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onUnFollowFail()
        }
    }


}