package com.mentorz.requester

import com.google.gson.annotations.SerializedName
import com.mentorz.feedback.FeedbackResponseListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection


class FeedbackRequester(description: String, scaleCount: Int, private var presenter: Any?, private var listener: FeedbackResponseListener?) : BaseRequester() {
    private var scaleCount: Int? = scaleCount
    private var description: String? = description

    fun execute() {
        doAsync {
            //run in background
            description = EncodingDecoding.encodeString(description!!)
            val mentorzApiResponse: MentorzApiResponse? = HttpController.sendFeedback(FeedbackRequest(description,scaleCount))
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onFeedbackSuccess()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onFeedbackFail()
        }
    }

    inner class FeedbackRequest(@field:SerializedName("description")
                                var description: String? = null,
                                @field:SerializedName("recommend_scale_count")
                                var scaleCount:Int?=null

    )


}